#include <jni.h>
#include "my-crypto-lib/src/versions/v20/privatekey_v20.h"
#include "my-crypto-lib/src/versions/v20/publickey_v20.h"

extern "C"
{

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_MainActivity_GetTestString
		(JNIEnv *env, jobject)
{
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::Generate(320, 512);
	Crypto::PublicKey::Ptr pubKey = key->GetPublicKey();
	Crypto::Data::Ptr data = Crypto::Data::Create("Test cryptostring");
	Crypto::Data::Ptr encryptedData = pubKey->EncryptData(data);
	Crypto::Data::Ptr decryptedData = key->DecryptData(encryptedData);
	return env->NewStringUTF(decryptedData->ToString().c_str());
}

JNIEXPORT void JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_GeneratePrivate
		(JNIEnv *env, jobject instance, int seed, int size)
{
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::Generate((unsigned long)seed, size);
	jstring keyData = env->NewStringUTF(key->ToData()->ToBase64().c_str());
	Crypto::Fingerprint fingerprint = key->GetFingerprint();
	jclass clazz = env->GetObjectClass(instance);
	env->SetObjectField(instance, env->GetFieldID(clazz, "data", "Ljava/lang/String;"), keyData);
	env->SetIntField(instance, env->GetFieldID(clazz, "fingerprint", "I"), static_cast<int>(fingerprint));
}
JNIEXPORT void JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_GeneratePublic
		(JNIEnv *env, jobject instance, jstring privateKeyBase64_)
{
	const char *privateKeyBase64 = env->GetStringUTFChars(privateKeyBase64_, 0);
	Crypto::Data::Ptr keyData = Crypto::Data::Create(privateKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
	Crypto::PublicKey::Ptr pubKey = key->GetPublicKey();
	Crypto::Fingerprint fingerprint = pubKey->GetFingerprint();
	env->ReleaseStringUTFChars(privateKeyBase64_, privateKeyBase64);
	jstring pubKeyData = env->NewStringUTF(pubKey->ToData()->ToBase64().c_str());
	jclass clazz = env->GetObjectClass(instance);
	env->SetObjectField(instance, env->GetFieldID(clazz, "data", "Ljava/lang/String;"), pubKeyData);
	env->SetIntField(instance, env->GetFieldID(clazz, "fingerprint", "I"), static_cast<int>(fingerprint));
}

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_EncryptMessage
		(JNIEnv *env, jobject instance, jstring message_)
{
	const char *message = env->GetStringUTFChars(message_, 0);

	jclass clazz = env->GetObjectClass(instance);
	jstring keyDataStr = (jstring)env->GetObjectField(instance, env->GetFieldID(clazz, "data", "Ljava/lang/String;"));
	const char *publicKeyBase64 = env->GetStringUTFChars(keyDataStr, 0);

	Crypto::Data::Ptr keyData = Crypto::Data::Create(publicKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PublicKey::Ptr key = Crypto::PublicKey_v20::CreateFromData(keyData);
	Crypto::Data::Ptr messageData = Crypto::Data::Create(message);
	std::string result = key->EncryptData(messageData)->ToBase64();

	env->ReleaseStringUTFChars(message_, message);

	return env->NewStringUTF(result.c_str());
}

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_DecryptMessage
		(JNIEnv *env, jobject instance, jstring cipher_)
{
	const char *cipher = env->GetStringUTFChars(cipher_, 0);

	jclass clazz = env->GetObjectClass(instance);
	jstring keyDataStr = (jstring)env->GetObjectField(instance, env->GetFieldID(clazz, "data", "Ljava/lang/String;"));
	const char *privateKeyBase64 = env->GetStringUTFChars(keyDataStr, 0);

	Crypto::Data::Ptr keyData = Crypto::Data::Create(privateKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
	Crypto::Data::Ptr cipherData = Crypto::Data::Create(cipher, Crypto::Data::Encoding::Base64);
	std::string result = key->DecryptData(cipherData)->ToString();

	env->ReleaseStringUTFChars(cipher_, cipher);

	return env->NewStringUTF(result.c_str());
}

} // extern "C"