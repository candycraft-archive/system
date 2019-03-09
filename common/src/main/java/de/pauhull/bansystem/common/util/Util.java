package de.pauhull.bansystem.common.util;

import java.util.List;
import java.util.Random;

/**
 * Created by Paul
 * on 28.11.2018
 *
 * @author pauhull
 */
public class Util {

    private static Random random = new Random();

    public static <E> E getRandomFromList(List<E> list) {
        return list.get(random.nextInt(list.size()));
    }

}
