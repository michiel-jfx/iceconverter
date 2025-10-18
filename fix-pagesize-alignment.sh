#!/bin/bash

set -e

LIB_FILE="target/gluonfx/aarch64-android/libIceCo.so"

if [ ! -f "$LIB_FILE" ]; then
    echo "Library not found: $LIB_FILE"
    exit 0  # do not fail if file doesn't exist yet
fi
cp "$LIB_FILE" "${LIB_FILE}.org"

echo "==== Current alignment ===="
readelf -l "$LIB_FILE" | grep LOAD

if [ -n "$ANDROID_NDK" ]; then
    OBJCOPY="${ANDROID_NDK}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-objcopy"

    if [ -f "$OBJCOPY" ]; then
        echo ""
        echo "==== Attempting to fix alignment with objcopy ===="

        "$OBJCOPY" \
            --set-section-alignment .text=16384 \
            --set-section-alignment .rodata=16384 \
            --set-section-alignment .data=16384 \
            --set-section-alignment .data.rel.ro=16384 \
            --set-section-alignment .bss=16384 \
            "$LIB_FILE" "${LIB_FILE}.fixed"

        if [ $? -eq 0 ]; then
            cp "${LIB_FILE}.fixed" "$LIB_FILE"
            echo ""
            echo "==== After fix ===="
            readelf -l "$LIB_FILE" | grep LOAD
        fi
    fi
fi
