package com.example.kotlinjetpack.model

import android.os.Parcel
import android.os.Parcelable

/**
 * desc: RightModel
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/5 19:41
 */
data class RightModel(
    val name: String?,
    val titleName: String?,
    val parentPos: Int =0,
    val isTitle: Boolean = false,
    val imgSrc: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(titleName)
        parcel.writeInt(parentPos)
        parcel.writeByte(if (isTitle) 1 else 0)
        parcel.writeString(imgSrc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RightModel> {
        override fun createFromParcel(parcel: Parcel): RightModel {
            return RightModel(parcel)
        }

        override fun newArray(size: Int): Array<RightModel?> {
            return arrayOfNulls(size)
        }
    }


}
