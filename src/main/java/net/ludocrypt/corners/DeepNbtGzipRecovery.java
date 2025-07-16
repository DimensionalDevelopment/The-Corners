package net.ludocrypt.corners;

import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;

public class DeepNbtGzipRecovery {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("D:\\Git Repos\\The-Corners\\src\\main\\resources\\data\\corners\\structures\\nbt\\communal_corridors\\communal_corridors_decorated\\communal_corridors_decorated_7.nbt");

        var nbt = NbtIo.read(inputFile);

        System.out.println(nbt);
    }
}