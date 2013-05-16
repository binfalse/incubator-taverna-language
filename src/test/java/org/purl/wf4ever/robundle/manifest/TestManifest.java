package org.purl.wf4ever.robundle.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestManifest {
    @Test
    public void populateFromBundle() throws Exception {
        Bundle bundle = exampleBundle();

        Path r = bundle.getRoot();
        URI base = r.toUri();

        Manifest manifest = new Manifest();
        manifest.populateFromBundle(bundle);

        List<String> uris = new ArrayList<>();
        for (PathMetadata s : manifest.aggregates) {
            uris.add(s.file.toASCIIString());
            Path path = uri2path(base, s.file);
            assertNotNull(path.getParent());
            assertEquals(Manifest.withSlash(path.getParent()), s.folder);
            if (s.file.equals(URI.create("f/nested/empty/"))) {
                continue;
                // Folder's don't need proxy and createdOn
            }
            System.out.println(s.file);
            assertEquals("urn", s.proxy.getScheme());
            UUID.fromString(s.proxy.getSchemeSpecificPart().replace("uuid:", ""));
            assertEquals(s.createdOn, Files.getLastModifiedTime(path));
        }
        assertFalse(uris.contains("mimetype"));
        assertFalse(uris.contains("META-INF"));
        assertTrue(uris.remove("hello.txt"));
        assertTrue(uris.remove("f/file1.txt"));
        assertTrue(uris.remove("f/file2.txt"));
        assertTrue(uris.remove("f/file3.txt"));
        assertTrue(uris.remove("f/nested/file1.txt"));
        assertTrue(uris.remove("f/nested/empty/"));
        assertTrue(uris.isEmpty());
    }

    private Path uri2path(URI base, URI uri) {
        URI fileUri = base.resolve(uri);
        return Paths.get(fileUri);
    }

    private Bundle exampleBundle() throws IOException {
        Path source;
        try (Bundle bundle = Bundles.createBundle()) {
            source = bundle.getSource();
            Path r = bundle.getRoot();
            Files.createFile(r.resolve("hello.txt"));
            Path f = r.resolve("f");
            Files.createDirectory(f);
            Files.createFile(f.resolve("file3.txt"));
            Files.createFile(f.resolve("file2.txt"));
            Files.createFile(f.resolve("file1.txt"));

            Path nested = f.resolve("nested");
            Files.createDirectory(nested);
            Files.createFile(nested.resolve("file1.txt"));

            Files.createDirectory(nested.resolve("empty"));
            bundle.setDeleteOnClose(false);
        }
        return Bundles.openBundle(source);
    }
}