package org.mdk.battle.mysqlagent.util;





import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;


public class YamlUtil {
	    private static String ROOT_PATH;

	    static {
	    	
	            StringBuilder sb = new StringBuilder();
	            /*
	            sb.append(System.getProperty("user.dir"))
	                .append(File.separator)
	                .append("source")
	                .append(File.separator)
	                .append("target")
	                .append(File.separator)
	                .append("classes")
	                .append(File.separator);
	            */
	            sb.append(System.getProperty("user.dir"))
                .append(File.separator)
                .append("conf")
                .append(File.separator);
	                
	            ROOT_PATH = sb.toString();
	            System.out.println(sb);
	    }

	    public static <T> T load(String fileName, Class<T> clazz) throws FileNotFoundException {
	        InputStreamReader fis = null;
	        try {
	            String path = ROOT_PATH + fileName;
	            File file=new File(path);  
	            if(file.exists()){
	            	Yaml yaml = new Yaml();
		            fis =new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
		            T obj = yaml.loadAs(fis, clazz);
		            if(obj !=null){
		            	return obj;
		            }
	            }
	            return null;
	        } finally {
	            if (fis != null) {
	                try {
	                    fis.close();
	                } catch (IOException ignored) {
	                }
	            }
	        }
	    }

	    public static String dump(Object obj) {
	        DumperOptions options = new DumperOptions();
	        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	        Representer representer = new Representer();
	        representer.addClassTag(obj.getClass(), Tag.MAP);
	        Yaml yaml = new Yaml(representer, options);
	        String str = yaml.dump(obj);
	        return str;
	    }



	    public static String getFileName(String configName, int version) {
	        return configName + "-" + version;
	    }

	    private static Integer parseConfigVersion(String fileName) {
	        return Integer.valueOf(fileName.substring(fileName.lastIndexOf("-") + 1));
	    }

	    /**
	     * 创建指定的文件夹
	     * @param directoryName
	     * @return 返回创建的文件夹路径
	     */
	    public static String createDirectoryIfNotExists(String directoryName) throws IOException {
	        String dirPath = ROOT_PATH + directoryName;
	        Path directory = Paths.get(dirPath);
	        if (!Files.exists(directory)) {
	            Files.createDirectories(directory);
	        }
	        return dirPath;
	    }

	    /**
	     * 清空文件夹
	     * @param directoryName
	     * @param filePrefix
	     * @throws IOException
	     */
	    public static void clearDirectory(String directoryName, String filePrefix) throws IOException {
	        String dirPath = ROOT_PATH + directoryName;
	        File dirFile = new File(dirPath);
	        Stream.of(dirFile.listFiles())
	                .filter(file -> {
	                    if (filePrefix == null) {
	                        return file != null;
	                    } else {
	                        return file != null && file.getName().startsWith(filePrefix);
	                    }
	                })
	                .forEach(file -> file.delete());
	    }  
}

