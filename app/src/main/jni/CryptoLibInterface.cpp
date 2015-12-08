#include "my-crypto-lib/src/versions/v20/privatekey_v20.h"
#include <jni.h>

extern "C" {

	JNIEXPORT jstring JNICALL Java_com_codextropy_mycryptoapp_MainActivity_GetTestString
			(JNIEnv *env, jobject object) {
		Crypto::PrivateKey::Ptr key = Crypto::PrivateKey_v20::Generate(320, 512);
		Crypto::PublicKey::Ptr pubKey = key->GetPublicKey();
		Crypto::Data::Ptr data = Crypto::Data::Create("Test cryptostring");
		Crypto::Data::Ptr encryptedData = pubKey->EncryptData(data);
		Crypto::Data::Ptr decryptedData = key->DecryptData(encryptedData);
		return env->NewStringUTF(decryptedData->ToString().c_str());
	}

}