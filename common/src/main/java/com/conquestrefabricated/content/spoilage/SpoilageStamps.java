package com.conquestrefabricated.content.spoilage;

/**
 * Remembers when something that holds food was last aged, across saves and chunk unloads, so the time it
 * spent out of the world can be made up when it comes back.
 *
 * <p>Only the loader can attach saved data to a block entity or a player, so each loader supplies the
 * storage with {@link #use}; until one does, nothing is remembered and nothing is caught up.</p>
 */
public final class SpoilageStamps {

    /** Where a loader keeps the stamps. A holder is a block entity or a player. */
    public interface Storage {
        /** The game time the holder was last aged at, or -1 if it has never been. */
        long get(Object holder);

        void set(Object holder, long gameTime);

        void clear(Object holder);
    }

    private static Storage storage = new Storage() {
        @Override
        public long get(Object holder) {
            return -1L;
        }

        @Override
        public void set(Object holder, long gameTime) {
        }

        @Override
        public void clear(Object holder) {
        }
    };

    private SpoilageStamps() {
    }

    public static void use(Storage loaderStorage) {
        storage = loaderStorage;
    }

    public static long get(Object holder) {
        return storage.get(holder);
    }

    public static void set(Object holder, long gameTime) {
        storage.set(holder, gameTime);
    }

    public static void clear(Object holder) {
        storage.clear(holder);
    }

    /**
     * How long, in ticks, a holder has gone unaged: a scan's worth if it has no stamp or the stamp is from
     * the future (a world restored from a backup), otherwise the time since its stamp.
     */
    public static long elapsed(Object holder, long now) {
        long last = storage.get(holder);
        return last < 0 || last > now ? Spoilage.INTERVAL : now - last;
    }
}
