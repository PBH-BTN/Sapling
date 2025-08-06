package com.ghostchu.tracker.sapling.util;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TorrentBencodeParser {
    private final Bencode bencode;
    private final Bencode iso8859;
    private final Map<String, Object> root;

    public TorrentBencodeParser(byte[] torrentBytes) {
        this.bencode = new Bencode(true);
        this.iso8859 = new Bencode(StandardCharsets.ISO_8859_1);
        this.root = this.bencode.decode(torrentBytes, Type.DICTIONARY);
    }
}
