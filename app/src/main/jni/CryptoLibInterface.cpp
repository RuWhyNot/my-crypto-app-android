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

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_MainActivity_GeneratePrivateKey
		(JNIEnv *env, jobject, int seed, int size)
{
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::Generate((unsigned long)seed, size);
	return env->NewStringUTF(key->ToData()->ToBase64().c_str());
}

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_MainActivity_GetPublicKey
		(JNIEnv *env, jobject, jstring privateKeyBase64_)
{
	const char *privateKeyBase64 = env->GetStringUTFChars(privateKeyBase64_, 0);
	Crypto::Data::Ptr keyData = Crypto::Data::Create(privateKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
	Crypto::PublicKey::Ptr pubKey = key->GetPublicKey();
	env->ReleaseStringUTFChars(privateKeyBase64_, privateKeyBase64);
	return env->NewStringUTF(pubKey->ToData()->ToBase64().c_str());
}

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_MainActivity_EncryptMessage
		(JNIEnv *env, jobject, jstring message_, jstring publicKeyBase64_)
{
	const char *message = env->GetStringUTFChars(message_, 0);
	const char *publicKeyBase64 = env->GetStringUTFChars(publicKeyBase64_, 0);

	Crypto::Data::Ptr keyData = Crypto::Data::Create(publicKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PublicKey::Ptr key = Crypto::PublicKey_v20::CreateFromData(keyData);
	Crypto::Data::Ptr messageData = Crypto::Data::Create(message);
	std::string result = key->EncryptData(messageData)->ToBase64();

	env->ReleaseStringUTFChars(message_, message);
	env->ReleaseStringUTFChars(publicKeyBase64_, publicKeyBase64);

	return env->NewStringUTF(result.c_str());
}

JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_MainActivity_DecryptMessage
		(JNIEnv *env, jobject, jstring cipher_, jstring privateKeyBase64_)
{
	const char *cipher = env->GetStringUTFChars(cipher_, 0);
	const char *privateKeyBase64 = env->GetStringUTFChars(privateKeyBase64_, 0);

	Crypto::Data::Ptr keyData = Crypto::Data::Create(privateKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
	Crypto::Data::Ptr cipherData = Crypto::Data::Create(cipher, Crypto::Data::Encoding::Base64);
	std::string result = key->DecryptData(cipherData)->ToString();

	env->ReleaseStringUTFChars(cipher_, cipher);
	env->ReleaseStringUTFChars(privateKeyBase64_, privateKeyBase64);

	return env->NewStringUTF(result.c_str());
}

} // extern "C"