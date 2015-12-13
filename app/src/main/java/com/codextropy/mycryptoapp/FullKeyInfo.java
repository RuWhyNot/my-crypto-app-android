package com.codextropy.mycryptoapp;


public class FullKeyInfo {
	public String data;
	public String name;
	public int fingerprint;

	public native void GeneratePrivate(int seed, int size);
	public native void GeneratePublic(String privateKeyData);

	public native String EncryptMessage(String message);
	public native String DecryptMessage(String cipher);

	public native void UpdatePublicFingerprint();
	public native void UpdatePrivateFingerprint();
}
