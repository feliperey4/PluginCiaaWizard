package pluginciaa.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Se encarga realizar las funciones relacionadas con 
 * documentos de tipo ZIP.
 * @author Felipe Rey
 *
 */
public class UtilsZip {

	private static final int BUFFER_SIZE = 4096;

	/**
	 * Funcion que descomprime documentos .zip y los coloca en un directorio especifico.
	 * @param fis Stream de .zip.
	 * @param destDirectory directorio donde se almacena el contenido descomprimido.
	 * @throws IOException
	 */
	public static void unZip(InputStream fis, String destDirectory)
			throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		
		ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(fis),
				Charset.forName("CP866"));
		ZipEntry entry = zipIn.getNextEntry();

		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Funcion para copiar el contenido de ZipStream en un archivo.
	 * @param zipIn Zip ha copiar.
	 * @param filePath directorio de copiado.
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		File file = new File(filePath).getParentFile();
		if (!file.exists())
			file.mkdirs();
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
