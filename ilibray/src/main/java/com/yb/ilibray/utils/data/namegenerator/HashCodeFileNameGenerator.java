//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yb.ilibray.utils.data.namegenerator;


public class HashCodeFileNameGenerator implements FileNameGenerator {
    public HashCodeFileNameGenerator() {
    }

    public String generate(String imageUri) {
        return String.valueOf(imageUri.hashCode());
    }
}
