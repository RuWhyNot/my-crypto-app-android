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
	return env->NewStringUTF(decryptedData->ToPlainString().c_str());
}

JNIEXPORT void JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_GeneratePrivate
		(JNIEnv *env, jobject instance, int seed, int size)
{
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::Generate((unsigned long)seed, size);
	jstring keyData = env->NewStringUTF(key->ToData()->GetBase64Data().c_str());
	Crypto::Fingerprint fingerprint = key->GetFingerprint();
	jclass clazz = env->GetObjectClass(instance);
	env->SetObjectField(instance, env->GetFieldID(clazz, "data", "Ljava/lang/String;"), keyData);
	env->SetIntField(instance, env->GetFieldID(clazz, "fingerprint", "I"), static_cast<int>(fingerprint));
}

JNIEXPORT void JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_GeneratePublic
		(JNIEnv *env, jobject instance, jstring privateKeyBase64_)
{
	const char *privateKeyBase64 = env->GetStringUTFChars(privateKeyBase64_, 0);
	Crypto::Data::Ptr keyData = Crypto::Data::Restore(privateKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
	Crypto::PublicKey::Ptr pubKey = key->GetPublicKey();
	Crypto::Fingerprint fingerprint = pubKey->GetFingerprint();
	env->ReleaseStringUTFChars(privateKeyBase64_, privateKeyBase64);
	jstring pubKeyData = env->NewStringUTF(pubKey->ToData()->GetBase64Data().c_str());
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

	Crypto::Data::Ptr keyData = Crypto::Data::Restore(publicKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PublicKey::Ptr key = Crypto::PublicKey_v20::CreateFromData(keyData);
	Crypto::Data::Ptr messageData = Crypto::Data::Create(message);
	std::string result = key->EncryptData(messageData)->GetBase64Data();

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

	Crypto::Data::Ptr keyData = Crypto::Data::Restore(privateKeyBase64, Crypto::Data::Encoding::Base64);
	Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
	Crypto::Data::Ptr cipherData = Crypto::Data::Restore(cipher, Crypto::Data::Encoding::Base64);
	std::string result = key->DecryptData(cipherData)->ToPlainString();

	env->ReleaseStringUTFChars(cipher_, cipher);

	return env->NewStringUTF(result.c_str());
}

JNIEXPORT int JNICALL Java_com_codextropy_mycryptoapp_KeyStorage_GetDataFingerprint
		(JNIEnv *env, jobject instance, jstring dataBase64_)
{
	const char *dataBase64 = env->GetStringUTFChars(dataBase64_, 0);

	Crypto::Data::Ptr data = Crypto::Data::Restore(std::string(dataBase64), Crypto::Data::Encoding::Base64);

	env->ReleaseStringUTFChars(dataBase64_, dataBase64);

	return static_cast<int>(data->GetFingerprint());
}

JNIEXPORT int JNICALL Java_com_codextropy_mycryptoapp_FullKeyInfo_UpdateFingerprint
		(JNIEnv *env, jobject instance)
{
	jclass clazz = env->GetObjectClass(instance);
	jstring keyDataStr = (jstring)env->GetObjectField(instance, env->GetFieldID(clazz, "data", "Ljava/lang/String;"));

	const char *privateKeyBase64 = env->GetStringUTFChars(keyDataStr, 0);
	Crypto::Data::Ptr keyData = Crypto::Data::Restore(privateKeyBase64, Crypto::Data::Encoding::Base64);

	Crypto::Data::Type dataType = keyData->GetType();
	if (dataType == Crypto::Data::Type::PublicKey) {
		Crypto::PublicKey::Ptr pubKey = Crypto::PublicKey_v20::CreateFromData(keyData);
		if (pubKey) {
			env->SetIntField(instance, env->GetFieldID(clazz, "fingerprint", "I"),
							 static_cast<int>(pubKey->GetFingerprint()));
		}
	}
	else if (dataType == Crypto::Data::Type::PrivateKey) {

		Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::CreateFromData(keyData);
		if (key) {
			env->SetIntField(instance, env->GetFieldID(clazz, "fingerprint", "I"),
							 static_cast<int>(key->GetFingerprint()));
		}
	}
}

JNIEXPORT jint JNICALL Java_com_codextropy_mycryptoapp_MainActivity_GetDataType
		(JNIEnv *env, jobject, jstring dataBase64_)
{
	const char *dataBase64 = env->GetStringUTFChars(dataBase64_, 0);

	Crypto::Data::Ptr data = Crypto::Data::Restore(dataBase64, Crypto::Data::Encoding::Base64);

	Crypto::Data::Type dataType = data->GetType();

	env->ReleaseStringUTFChars(dataBase64_, dataBase64);

	return Crypto::Data::GetByteFromType(dataType);
}

} // extern "C"