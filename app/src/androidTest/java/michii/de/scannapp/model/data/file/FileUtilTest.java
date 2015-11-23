package michii.de.scannapp.model.data.file;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 09.07.2015
 */
@RunWith(JUnit4.class)
public class FileUtilTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Test
    public void testRenameFile() throws IOException {
        File createdFile = folder.newFile("myfile.txt");
        assertTrue(createdFile.exists());
        assertEquals("myfile.txt", createdFile.getName());

        assertTrue(FileUtil.renameFile(createdFile.getPath(), "neuerTitel"));
        assertEquals("neuerTitel.txt", createdFile.getName());
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