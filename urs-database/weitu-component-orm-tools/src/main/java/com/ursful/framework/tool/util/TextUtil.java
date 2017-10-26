package com.ursful.framework.tool.util;

import com.ursful.framework.tool.util.DataType;
import com.ursful.framework.tool.OrderedProperties;
import com.ursful.framework.tool.db.DBUtil;
import com.ursful.framework.tool.db.DatabaseType;
import com.ursful.framework.tool.db.Information;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by ynice on 3/28/17.
 */
public class TextUtil {

    public static void create(
            Information info,String dbName,List<String> tables, String packageName,String folderMenu,
            String javafolder,String webfolder, String commonPackage){
        try {
            String pcn = webfolder + File.separator + "js" + File.separator + "lang" + File.separator + "Messages_cn.properties";
            String pen = webfolder + File.separator + "js" + File.separator + "lang" + File.separator + "Messages_en.properties";
            //List<String> tables = DBUtil.getTables(info, dbName);
            OrderedProperties prop = new OrderedProperties();
            File file1 = new File(pcn);
            if(!file1.getParentFile().exists()){
                file1.getParentFile().mkdirs();
            }

            if(file1.exists()){
                FileInputStream fis = new FileInputStream(pcn);
                prop.load(fis);
                fis.close();
            }

            OrderedProperties prop2 = new OrderedProperties();

            File file2 = new File(pen);
            if(!file2.getParentFile().exists()){
                file2.getParentFile().mkdirs();
            }

            if(file2.exists()){
                FileInputStream fis = new FileInputStream(pen);
                prop2.load(fis);
                fis.close();
            }


            for(String tableName : tables){

                //createJsHtml(info, dbName, tableName, webfolder);

                Map [] ens = TextUtil.createJavaFiles(info, dbName, javafolder, packageName, tableName, commonPackage);

                String iname = TextUtil.getTableWithoutT(tableName).replace("_", ".");
                prop.putAll(ens[0]);
                prop.put(iname + ".mgmt", DBUtil.getTableComment(info, tableName, dbName) +  "管理");
                prop2.putAll(ens[1]);
                prop2.put(iname + ".mgmt", TextUtil.getEnName(TextUtil.getTableWithoutT(tableName)) +  " Mgmt");

            }

            prop.store(new FileOutputStream(pcn), null);
            prop2.store(new FileOutputStream(pen), null);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static  void createJsHtml(Information info, String dbName, String tableName, String webFolder){
        String fileName = TextUtil.getClassNameFromTableName(tableName).toLowerCase();

        System.out.println("--->"  + tableName);
        System.out.println("==>" + getServiceNameFromTableName(tableName));

        TextUtil.createHtml(TextUtil.createHtmlContent("model", fileName), fileName, webFolder + File.separator + "admin"+ File.separator + getServiceNameFromTableName(tableName).replace("/", File.separator));
        TextUtil.createJs(TextUtil.jsContent(info, dbName, tableName), fileName, webFolder + File.separator + "adminjs");

        String dest = webFolder + File.separator + "adminjs" + File.separator + fileName + "override.js";

        File file = new File(dest);
        if(!file.exists()) {
            TextUtil.createJs(TextUtil.jsOverrideContent(info, dbName, tableName), fileName + "override", webFolder  + File.separator + "adminjs");
        }
        // sb.append("//var " + beanName + " = new Base(\"main\", \"page\", parent, " + beanNameConfig + ");\r\n");


        System.out.println(fileName);
    }

    public static void main(String[] args) {
        System.out.println(File.separatorChar);
        System.out.println(File.separator);
    }

    public static Map []  createJavaFiles(Information info, String dbName, String folder, String packageName, String tableName, String commonPackage){

        Map<String, String> i18nCn = new HashMap<String, String>();
        Map<String, String> i18nEn = new HashMap<String, String>();
        try {

            String dest = folder;// + File.separator + packageName.replaceAll("[.]", "/".equals(File.pathSeparator)?"/":"\\");
            String pks[] = packageName.split("[.]");
            String commonDest = folder;
            String cpks [] = commonPackage.split("[.]");
            for(String pk : pks){
                dest += File.separator + pk;
            }
            for(String cpk : cpks){
                commonDest += File.separator + cpk;
            }

            DatabaseType databaseType = DatabaseType.getDatabaseType(info.getType());
            //List<String> tables = getTables();
            System.out.println("Database: " + databaseType.name() + " : " + tableName);
            System.out.println("=======>" + info.getUrl());
            System.out.println(info);
            Map<String, String> columns = DBUtil.getTableColumns(info, tableName);
            System.out.println(columns);
            String beanName = TextUtil.getClassNameFromTableName(tableName);

            String serviceTitle = DBUtil. getTableComment(info, tableName, dbName);

            System.out.println("serviceTitle:"  + serviceTitle);

            StringBuffer model = new StringBuffer();

            if(tableName.toLowerCase().startsWith("sys_")) {
                model.append("package " + commonPackage + ".entity.sys;\r");
            }else{
                model.append("package " + commonPackage + ".entity;\r");
            }
            model.append("\r");
            if(columns.values().contains("java.util.Date")){
                model.append("import java.util.Date;\r");
            }
            model.append("import java.io.Serializable;\r");

            model.append("import com.weitu.framework.core.annotation.RsEntity;\r");
            model.append("import com.weitu.framework.component.orm.annotation.RdColumn;\r");
            model.append("import com.weitu.framework.component.orm.annotation.RdTable;\r");
            model.append("{1}");
            model.append("\r");
            model.append("//" + serviceTitle + "\r");
            model.append("@RsEntity\r");
            model.append("@RdTable(name = \""+ tableName.toUpperCase() + "\")\r");
            model.append("public class " + beanName + " implements Serializable{\r");

            StringBuffer staticString = new StringBuffer();
            StringBuffer fieldString = new StringBuffer();
            StringBuffer method = new StringBuffer();

            staticString.append("    private static final long serialVersionUID = 1L;\r");



            String large = "";
            Map<String, String> colComments = DBUtil.getTableColumnComment(info, tableName, dbName);
            Map<String, Boolean> colNulls = DBUtil.getTableColumnNull(info, tableName, dbName);


             System.out.println(colComments+ "==xx=>" +colNulls);

            for(String columnName : columns.keySet()){

                String className = columns.get(columnName);

                columnName = columnName.toUpperCase();


                String fieldName =  TextUtil.getFieldNameFromColumnName(columnName);
                staticString.append("    //"  + colComments.get(columnName)  + " (" +
                        (colNulls.get(columnName)?"NULL":"NOT NULL")+")" + "\r");
                staticString.append("    public static final String T_" + columnName.toUpperCase() + " = \"" +
                        columnName + "\";\r");

                String tn = getTableWithoutT(tableName + "_").replace("_", ".") + fieldName;
                i18nCn.put(tn , colComments.get(columnName).split(";")[0]);
                i18nEn.put(tn, getEnName(columnName));


                boolean isLarge = false;
                if(className.startsWith("blob")){
                    isLarge = true;
                    className = className.substring(5);
                }

                DataType dt = DataType.getDataType(className);
                String type = dt.name().substring(0, 1) + dt.name().substring(1, dt.name().length()).toLowerCase();


                boolean isPrimarykey = DBUtil. isPrimaryKey(info, tableName, columnName, dbName);

                fieldString.append("\r");
                if(isLarge){
                    fieldString.append("    @RdLargeString\r");
                    large += "import com.weitu.framework.component.orm.annotation.RdLargeString;\r";
                }
                if(isPrimarykey){
                    large += "import com.weitu.framework.component.orm.annotation.RdId;\r";
                    fieldString.append("    @RdId\r");
                    fieldString.append("    @RdColumn(name = T_" + columnName.toUpperCase() + ", unique = true)\r");
                }else{

                    fieldString.append("    @RdColumn(name = T_" + columnName.toUpperCase() + ")\r");
                }




                fieldString.append("    private " + type + " " + fieldName + ";\r");

                method.append("\r\r    public " + type + " " + TextUtil.getGetNameFromColumnName(columnName) + "(){\r");
                method.append("        return this." + fieldName + ";\r    }\r\r");
                method.append("    public void " + TextUtil.getSetNameFromColumnName(columnName) + "("+ type + " " + fieldName +"){\r");
                method.append("        this." + fieldName + " = " + fieldName + ";\r    }");

                System.out.println("====>" + dt);
                if(!isPrimarykey){
                    if(dt == DataType.DATE){
                        String fieldNames = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                        fieldString.append("    private " + type + " start" + fieldNames + ";\r");
                        method.append("\r\r    public " + type + " " + TextUtil.getGetNameFromColumnName("start_" + columnName) + "(){\r");
                        method.append("        return this.start" + fieldNames + ";\r    }\r\r");
                        method.append("    public void " + TextUtil.getSetNameFromColumnName("start_" + columnName) + "("+ type + " start" + fieldNames +"){\r");
                        method.append("        this.start" + fieldNames + " = start" + fieldNames + ";\r    }");
                        fieldString.append("    private " + type + " end" + fieldNames + ";\r");
                        method.append("\r\r    public " + type + " " + TextUtil.getGetNameFromColumnName("end_" + columnName) + "(){\r");
                        method.append("        return this.end" + fieldNames + ";\r    }\r\r");
                        method.append("    public void " + TextUtil.getSetNameFromColumnName("end_" + columnName) + "("+ type + " end" + fieldNames +"){\r");
                        method.append("        this.end" + fieldNames + " = end" + fieldNames + ";\r    }");
                    }else if(dt == DataType.INTEGER || dt == DataType.LONG || dt == DataType.DOUBLE){
                        String fieldNames = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                        fieldString.append("    private " + type + " min" + fieldNames + ";\r");
                        method.append("\r\r    public " + type + " " + TextUtil.getGetNameFromColumnName("min_" + columnName) + "(){\r");
                        method.append("        return this.min" + fieldNames + ";\r    }\r\r");
                        method.append("    public void " + TextUtil.getSetNameFromColumnName("min_" + columnName) + "("+ type + " min" + fieldNames +"){\r");
                        method.append("        this.min" + fieldNames + " = min" + fieldNames + ";\r    }");
                        fieldString.append("    private " + type + " max" + fieldNames + ";\r");
                        method.append("\r\r    public " + type + " " + TextUtil.getGetNameFromColumnName("max_" + columnName) + "(){\r");
                        method.append("        return this.max" + fieldNames + ";\r    }\r\r");
                        method.append("    public void " + TextUtil.getSetNameFromColumnName("max_" + columnName) + "("+ type + " max" + fieldNames +"){\r");
                        method.append("        this.max" + fieldNames + " = max" + fieldNames + ";\r    }");
                    }
                }

            }

            model.append("\r" + staticString.toString() + "\r");
            model.append(fieldString.toString());
            model.append(method.toString() + "\r");


            model.append("}");

            System.out.println("folder:" + folder);
            if(tableName.toLowerCase().startsWith("sys_")){
                TextUtil.createJava(model.toString().replace("{1}", large), beanName, commonDest + File.separator + "entity" + File.separator + "sys");
                TextUtil.createJavaExistNotCreate(TextUtil.createServiceInterfaceContent("iservice", commonPackage, beanName, tableName), "I" + beanName + "Service", commonDest + File.separator + "service" + File.separator + "sys");
            }else{
                TextUtil.createJava(model.toString().replace("{1}", large), beanName, commonDest + File.separator + "entity");
                TextUtil.createJavaExistNotCreate(TextUtil.createServiceInterfaceContent("iservice", commonPackage, beanName, tableName), "I" + beanName + "Service", commonDest + File.separator + "service");
            }

            TextUtil.createJavaExistNotCreate(TextUtil.createServiceImplementContent("serviceimpl", packageName, beanName, tableName, commonPackage), beanName + "ServiceImpl", dest + File.separator + "service");
            //TextUtil.createJava(TextUtil.createServicefulContent("serviceful", packageName, beanName, serviceTitle, tableName), beanName + "Serviceful", dest + File.separator + "serviceful");


            String tn = getTableWithoutT(tableName + "_").replace("_", ".");
            i18nCn.put(tn + "save","添加" + serviceTitle);
            i18nCn.put(tn + "update","编辑" + serviceTitle);
            i18nEn.put(tn + "save","Add New " + getEnName(getTableWithoutT(tableName)));
            i18nEn.put(tn + "update","Edit " + getEnName(getTableWithoutT(tableName)));

            System.out.println(i18nCn);
            System.out.println(i18nEn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Map[]{i18nCn, i18nEn};
    }

    public static String getEnName(String columnName){
        String [] names = columnName.split("_");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < names.length; i++){
            String name = names[i];

            sb.append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());

            if(i < names.length - 1){
                sb.append(" ");
            }

        }
        return sb.toString();
    }


    public static String getTableWithoutT(String tableName){
        if(tableName.toLowerCase().startsWith("t_")){
            return tableName.substring(2).toLowerCase();
        }
        return tableName.toLowerCase();
    }

    public static String getFileContent(String name){

        ClassLoader loader = TextUtil.class.getClassLoader();

        InputStream ins = loader.getResourceAsStream("deploy.properties");


        InputStream is = loader.getResourceAsStream(name + ".txt");

        //BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));

        StringBuffer sb = new StringBuffer();
        try {
            //BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            while((line = br.readLine()) != null){
                sb.append(line+"\r");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getGetNameFromColumnName(String columnName){
        return getSetOrGetNameFromColumnName("get", columnName);
    }

    public static String getSetNameFromColumnName(String columnName){
        return getSetOrGetNameFromColumnName("set", columnName);
    }

    private static String getSetOrGetNameFromColumnName(String setOrGet, String columnName){
        String [] names = columnName.split("_");
        StringBuffer sb = new StringBuffer(setOrGet);
        for(int i = 0; i < names.length; i++){
            String name = names[i];
            sb.append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    //user_name => userName
    public static String getFieldNameFromColumnName(String columnName){
        String [] names = columnName.split("_");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < names.length; i++){
            String name = names[i];
            if(sb.length() == 0){
                sb.append(name.toLowerCase());
            }else{
                sb.append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String createServicefulContent(String name, String packageName, String beanName, String serviceTitle, String tableName){
        String result = TextUtil.getFileContent(name);

        String fieldName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        result = result.replace("{packageName}", packageName);
        result = result.replace("{beanName}", beanName);
        result = result.replace("{serviceName}", getServiceNameFromTableName(tableName));
        result = result.replace("{serviceTitle}", serviceTitle);

        //if(menuURL != null && !"".equals(menuURL)){
            //result = result.replace("{menuPackage}", "import RsMenu;");
            tableName = tableName.toLowerCase();
            if(tableName.startsWith("t_")){
                tableName = tableName.substring(2);
            }
            result = result.replace("{menu}", "@RsMenu(name=\""+ tableName.replace("_", ".") +".mgmt\", url=\"/admin/"+getServiceNameFromTableName(tableName)+".html\")");
        //}else{
        //    result = result.replace("{menuPackage}", "");
        //    result = result.replace("{menu}", "");
        //}
        result = result.replace("{fieldName}", fieldName);
        return result;
    }


    //t_sys_user = > /sys/user;
    public static String getServiceNameFromTableName(String tableName){
        tableName = tableName.toLowerCase();
        if(tableName.startsWith("t_")){
            tableName = tableName.substring(1);
        }
        return tableName.replaceAll("[_]","/");
    }

    public static String createServiceImplementContent(String name, String packageName, String beanName, String tableName, String commonPackage){
        String result = TextUtil.getFileContent(name);
        String fieldName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        result = result.replace("{packageName}", packageName);
        result = result.replace("{beanName}", beanName);
        result = result.replace("{fieldName}", fieldName);
        result = result.replace("{commonPackage}", commonPackage);
        result = result.replace("{sys}", tableName.toLowerCase().startsWith("sys_")?".sys":"");
        return result;
    }

    public static String createServiceInterfaceContent(String name, String packageName, String beanName, String tableName){
        String result = TextUtil.getFileContent(name);
        String fieldName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        result = result.replace("{packageName}", packageName);
        result = result.replace("{beanName}", beanName);
        result = result.replace("{fieldName}", fieldName);
        result = result.replace("{sys}", tableName.toLowerCase().startsWith("sys_")?".sys":"");
        return result;
    }

    public static String createHtmlContent(String name, String fieldName){
        String result = TextUtil.getFileContent(name);
        result = result.replace("{fieldName}", fieldName);
        result = result.replace("{version}", new Random().nextInt(100000) + "");
        return result;
    }

    public static void createJava(String content, String beanName, String path){
        String dest = path + File.separator + beanName + ".java";
        createFile(content, dest);
    }

    public static void createJavaExistNotCreate(String content, String beanName, String path){
        String dest = path + File.separator + beanName + ".java";
        if(!new File(dest).exists()) {
            createFile(content, dest);
        }
    }

    public static void createHtml(String content, String fileName, String path){
        String dest = path + ".html";
        createFile(content, dest);
    }

    public static void createJs(String content, String fileName, String path){
        String dest = path + File.separator + fileName + ".js";
        createFile(content, dest);
    }

    private static void createFile(String content, String path){
        System.out.println("Dest: " +path);
        File root = new File(path);
        if(!root.getParentFile().exists()){
            root.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fs = new FileOutputStream(root);
            OutputStreamWriter fos = new OutputStreamWriter(fs, "utf-8");
            fos.write(content);
            fos.flush();
            fos.close();
            fs.flush();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //t_sys_user => SysUser
    public static String getClassNameFromTableName(String tableName){
        String [] names = tableName.split("_");
        if(names.length == 1){
            return names[0].substring(0,1).toUpperCase() + names[0].substring(1).toLowerCase();
        }
        int k = 1;
        if("sys".equalsIgnoreCase(names[0].trim())){
            k = 0;
        }
        StringBuffer sb = new StringBuffer();
        for(int i = k; i < names.length; i++){
            String name = names[i];
            sb.append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    //t_sys_user => sysUser
    public static String getFieldNameFromTableName(String tableName){
        String [] names = tableName.split("_");
        if(names.length == 1){
            return names[0].substring(0,1).toUpperCase() + names[0].substring(1).toLowerCase();
        }
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        int k = 1;
        if("sys".equalsIgnoreCase(names[0].trim())){
            k = 0;
        }
        for(int i = k; i < names.length; i++){
            String name = names[i];
            if(isFirst){
                isFirst = false;
                sb.append(name.toLowerCase());
                continue;
            }
            sb.append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public static String jsContent(Information info, String dbName, String tableName){

        Map<String, String> columns =  DBUtil.getTableColumns(info, tableName);
        System.out.println(columns);

        String shortTableName = tableName.toLowerCase();
        if(shortTableName.startsWith("t_")){
            shortTableName = shortTableName.substring(2);
        }
        StringBuffer sb = new StringBuffer();
        String beanName = getFieldNameFromTableName(shortTableName);
        String beanNameConfig =  beanName+ "Config";


        //boolean isPrimarykey = allowNull(tableName, columnName);


        sb.append("(function(window, undefined) {\r\n");
        sb.append("    " + beanNameConfig + " = {tableName:'"+ shortTableName + "'};\n");

        sb.append("    " + beanNameConfig + "['search'] = [];\r\n");

        StringBuffer content = new StringBuffer();
        int length = 100/columns.size();
        StringBuffer priContent = new StringBuffer();

        StringBuffer save = new StringBuffer();
        StringBuffer saveTmp = new StringBuffer();

        String primaryKey = null;

        StringBuffer update = new StringBuffer();
        StringBuffer updateTmp = new StringBuffer();



        for(String columnName : columns.keySet()){

            String className = columns.get(columnName);

            boolean isPrimarykey = DBUtil.isPrimaryKey(info, tableName, columnName, dbName);
            String bn = getFieldNameFromTableName(columnName);
            if(isPrimarykey){
                update.append("        {name:\"" + bn + "\", type:'hidden'}");
                update.append(",\r\n");

                save.append("        {name:\"" + bn + "\", nullable:false}");
                save.append(",\r\n");

                primaryKey = bn;

                priContent.append("        {name:\"" + bn + "\", width:'" + length + "%'}");
                priContent.append(",\r\n");
            }else{

                String dt = DataType.getDataType(className).name().toLowerCase();


                content.append("        {name:\"" + bn + "\", width:'" + length + "%'}");
                content.append(",\r\n");

                boolean isn = DBUtil. isNull(info, tableName, columnName, dbName);

                System.out.println("dat...." + dt);

                if("date".equals(dt)){
                    saveTmp.append("        {name:\"" + bn + "\", type:\"" + dt + "\", nullable:" + isn + "}");
                }else {
                    saveTmp.append("        {name:\"" + bn + "\", nullable:" + isn + "}");
                }
                saveTmp.append(",\r\n");

            }


        }

        if(primaryKey != null){
            sb.append("    " + beanNameConfig + ".primaryKey = '"+ primaryKey + "';\n");
        }
        sb.append("    " + beanNameConfig + "['grid'] = [\r\n");

        save.append(saveTmp);
        String sc = save.substring(0, save.length() - 3) + "\r\n";

        update.append(saveTmp);
        String uc = update.substring(0, update.length() - 3) + "\r\n";


        priContent.append(content);

        String ct = priContent.substring(0, priContent.length() - 3) + "\r\n";
        sb.append(ct);

        sb.append("    " + "];\r\n");

        sb.append("    " + beanNameConfig + "['save'] = [\r\n");
        sb.append(sc);
        sb.append("    " + "];\r\n");

        sb.append("    " + beanNameConfig + "['saveValidation'] = function(name, value){\r\n");
        sb.append("        return null;\r\n");
        sb.append("    };\r\n");

        if(primaryKey != null){
            sb.append("    " + beanNameConfig + "['update'] = [\r\n");
            sb.append(uc);
            sb.append("    " + "];\r\n");

            sb.append("    " + beanNameConfig + "['updateValidation'] = function(name, value){\r\n");
            sb.append("        return null;\r\n");
            sb.append("    };\r\n");
        }
        sb.append("    " + beanNameConfig + "['buttons'] = [");
        /*if(primaryKey != null){
            sb.append("'batch.delete',");
        }*/
        sb.append("'save'");
        if(primaryKey != null){
            sb.append(",'update','delete'");
        }
        sb.append("];\r\n");

        sb.append("    window." + beanNameConfig + " = " + beanNameConfig + ";\r\n");
        sb.append("})(window);\r\n");

        sb.append("//var " + beanName + " = new Base(\"main\", \"page\", parent, " + beanNameConfig + ");\r\n");

        return sb.toString();
    }


    public static String jsOverrideContent(Information info, String dbName, String tableName){


        String shortTableName = tableName.toLowerCase();
        if(shortTableName.startsWith("t_")){
            shortTableName = shortTableName.substring(2);
        }
        StringBuffer sb = new StringBuffer();
        String beanName = getFieldNameFromTableName(shortTableName);
        String beanNameConfig =  beanName+ "Config";

        sb.append("$(document).ready(function(){\r");
        sb.append("    var " + beanName + " = new Base(\"main\", \"page\", parent, " + beanNameConfig + ");\r");
        sb.append("});");
        return sb.toString();
    }
}
