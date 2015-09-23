package pluginciaa.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * 
 * @author Felipe Rey
 *
 */
public class UtilsCiaa {

	public static final String PROYECTOS="projects";
	
	public static final String PROYECTO_GENERICO="base_general";
	
	public static final String PROYECTO_METAL="base_metal";
	
	public static final String MAKE_FILE_MINE="Makefile.mine";
	
	public static final String MARCA_BOARD="{*BOARD*}";
	
	public static final String MARCA_PROYECTO="{*PROYECTO*}";
	
	public static final String MARCA_ID="{*ID*}";
	
	public static final String DIR_SETTINGS=".settings";
	
	public static final String FILE_SETTINGS="org.eclipse.cdt.core.prefs";
	
	public static final String LISTA_BOARD []={
		"ciaa_sim_ia32",
		"ciaa_sim_ia64",
		"edu_ciaa_nxp",
		"ciaa_nxp",
		"ciaa_fsl",
		"ciaa_pic"
	};

	

	public static void CopiarDirectorio(File dirOrigen, File dirDestino) throws Exception { 
		try {
			if (dirOrigen.isDirectory()) { 
				if (!dirDestino.exists())
					dirDestino.mkdir(); 
	 
				String[] hijos = dirOrigen.list(); 
				for (int i=0; i < hijos.length; i++) { 
					CopiarDirectorio(new File(dirOrigen, hijos[i]), 
						new File(dirDestino, hijos[i])); 
				} 
			} else { 
				Copiar(dirOrigen, dirDestino); 
			} 
		} catch (Exception e) {
			throw e;
		} 
	} 
	 
	public static void Copiar(File dirOrigen, File dirDestino) throws Exception { 	 
		InputStream in = new FileInputStream(dirOrigen); 
		OutputStream out = new FileOutputStream(dirDestino); 
	 
		byte[] buffer = new byte[1024];
		int len;
	 
		try {
			while ((len = in.read(buffer)) > 0) { 
				out.write(buffer, 0, len); 
			} 
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			in.close(); 
			out.close(); 
		} 
	}
	 
	/**
	 * Funcion para establecer y cambiar el nombre de la carpeta de proyecto.
	 * @param nombreProj Nombre del proyecto.
	 * @param dirBase Directorio de proyecto C.
	 * @throws IOException
	 */
	public static void setNombreProyecto(String nombreProj,Path dirBase,boolean isGenerico) throws IOException{		
		Path path=dirBase.resolve(PROYECTOS).resolve(isGenerico?PROYECTO_GENERICO:PROYECTO_METAL);
		Path pathDelete=dirBase.resolve(PROYECTOS).resolve(isGenerico?PROYECTO_METAL:PROYECTO_GENERICO);
		Path pathNuevo=dirBase.resolve(PROYECTOS).resolve(nombreProj);
		File file= path.toFile();
		
		if(isGenerico){
			Path pathAux=path.resolve("etc").resolve("base.oil");
			Path pathAuxNuevo=path.resolve("etc").resolve(nombreProj+".oil");
			if(!pathAux.toFile().renameTo(pathAuxNuevo.toFile()))throw new IOException("Error cambiando el nombre del proyecto.");
		}
		
		if(!file.renameTo(pathNuevo.toFile()))throw new IOException("Error cambiando el nombre del proyecto.");
		deleteFile(pathDelete.toFile());
	}
	
	public static void deleteFile(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					deleteFile(fileDelete);
				}
				if (file.list().length == 0)file.delete();
			}
		} else file.delete();
	}

	/**
	 * Funcion para actualizar el contenido del archivo .mine. Coloca Board y nombre del proyecto.
	 * @param dirBase Directorio del proyecto.
	 * @param board Valor de Board para remplazar.
	 * @param nombreProyecto Valor del nombre del proyecto a remplazar.
	 * @throws IOException
	 */
	public static void actualizarMime(Path dirBase,String board, String nombreProyecto) throws IOException{
		Path path = dirBase.resolve(MAKE_FILE_MINE);
		File file = path.toFile();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		in.read();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		String linea;		
		boolean flagBoard=false;
		boolean flagProyecto=false;
		while((linea=in.readLine())!=null){			
			//Busca proyecto
			if(linea.contains(MARCA_PROYECTO) && !flagProyecto){
				flagProyecto=true;
				linea=linea.replace(MARCA_PROYECTO, nombreProyecto);
			}
			else if(!flagBoard && linea.contains(MARCA_BOARD)){//Board
				flagBoard=false;
				linea=linea.replace(MARCA_BOARD, board);
			}
			
			out.append(linea);
			out.newLine();
		}
		in.close();
		out.flush();
		out.close();
	}
	
	public static void setVariablesEntorno(Path dirBase,String id,String board) throws IOException{
		Path path = dirBase.resolve(DIR_SETTINGS).resolve(FILE_SETTINGS);
		File file = path.toFile();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		in.read();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		String linea;		
		while((linea=in.readLine())!=null){			
			if(linea.contains(MARCA_ID))linea=linea.replace(MARCA_ID, id);
			if(linea.contains(MARCA_BOARD))linea=linea.replace(MARCA_BOARD, board);
			out.append(linea);
			out.newLine();
		}
		in.close();
		out.flush();
		out.close();
	}
}
