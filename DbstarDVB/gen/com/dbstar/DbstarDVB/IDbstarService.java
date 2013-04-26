/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\LuoShidong\\Development\\dbstar\\DbstarDVB\\src\\com\\dbstar\\DbstarDVB\\IDbstarService.aidl
 */
package com.dbstar.DbstarDVB;
public interface IDbstarService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.dbstar.DbstarDVB.IDbstarService
{
private static final java.lang.String DESCRIPTOR = "com.dbstar.DbstarDVB.IDbstarService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dbstar.DbstarDVB.IDbstarService interface,
 * generating a proxy if needed.
 */
public static com.dbstar.DbstarDVB.IDbstarService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.dbstar.DbstarDVB.IDbstarService))) {
return ((com.dbstar.DbstarDVB.IDbstarService)iin);
}
return new com.dbstar.DbstarDVB.IDbstarService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_initDvbpush:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.initDvbpush();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_uninitDvbpush:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.uninitDvbpush();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendCommand:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
android.content.Intent _result = this.sendCommand(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.dbstar.DbstarDVB.IDbstarService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int initDvbpush() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_initDvbpush, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int uninitDvbpush() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_uninitDvbpush, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.content.Intent sendCommand(int cmd, java.lang.String buf, int len) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.Intent _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd);
_data.writeString(buf);
_data.writeInt(len);
mRemote.transact(Stub.TRANSACTION_sendCommand, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.content.Intent.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_initDvbpush = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_uninitDvbpush = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_sendCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public int initDvbpush() throws android.os.RemoteException;
public int uninitDvbpush() throws android.os.RemoteException;
public android.content.Intent sendCommand(int cmd, java.lang.String buf, int len) throws android.os.RemoteException;
}
