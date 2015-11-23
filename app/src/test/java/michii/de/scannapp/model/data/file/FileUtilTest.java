package michii.de.scannapp.model.data.file;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 01.07.2015
 */
public class FileUtilTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreateFile() throws IOException {
//        File createdFolder = folder.newFolder("newFolder");
//        File createdFile = folder.newFile("myfile.txt");
//        FileUtil.createFile(createdFolder, "myfile.txt");
//        assertTrue(createdFile.exists());
    }

    @Test
    public void testDeleteFile() throws IOException {
        File createdFolder = folder.newFolder("newFolder");
        File createdFile = folder.newFile("myfile.txt");
        assertTrue(createdFile.exists());

        FileUtil.deleteFileNoThrow(createdFile.getAbsolutePath());
        assertTrue("File should be delete", !createdFile.exists());
    }

    @Test
    public void testRenameFile() throws IOException {
        File createdFolder = folder.newFolder("newFolder");
        File createdFile = folder.newFile("myfile.txt");
        assertTrue(createdFile.exists());
    }

    @Test
    public void testRemoveExtension() throws Exception {
        File createdFolder = folder.newFolder("newFolder");

        File createdFile = folder.newFile("myfile.txt");
        assertTrue(createdFile.exists());
        String pathWithoutExtension = FileUtil.removeExtension(createdFile.getAbsolutePath());
        assertEquals(pathWithoutExtension, "myfile");
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testGetExtension() throws Exception {
        File createdFolder = folder.newFolder("newFolder");
        File createdFile = folder.newFile("myfile.jpg");
        assertTrue(createdFile.exists());
        String extension = FileUtil.getExtension(createdFile.getAbsolutePath());
        assertEquals(extension, "jpg");

    }
}