package com.codextropy.mycryptoapp;


public final class FullKeyInfo {
	public String data;
	public String name;
	public int fingerprint;
	public int dbId;
	public KeyStorage.Type keyType;

	public native void GeneratePrivate(int seed, int size);
	public native void GeneratePublic(String privateKeyData);

	public native String EncryptMessage(String message);
	public native String DecryptMessage(String cipher);

	public native void UpdateFingerprint();

	public FullKeyInfo()
	{
		dbId = -1;
	}
}
