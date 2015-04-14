package raytrace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RaytraceServer implements Runnable{
	public void run(){
		ServerSocket socket;
		try{
			socket=new ServerSocket(9090);
			while(true){
				Socket newConnection=socket.accept();
				Thread thread=new Thread(new ConnectionHandler(newConnection));
				thread.start();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
enum HTTPMethod {GET, POST}
enum HTTPStatusCode{
	OK(200, "OK"), NotFound(404, "Not Found"), ServerError(500, "Server Error");
	
	private int code;
	private String description;
	
	private HTTPStatusCode(int code, String description){
		this.code=code;
		this.description=description;
	}
	public int getCode(){
		return this.code;
	}
	public String getDescription(){
		return this.description;
	}
	
}
class ConnectionHandler implements Runnable{
	private Socket socket;
	private Request request;
	private Response response;
	private ArrayList<String> lines;
	public ConnectionHandler(Socket toHandle){
		this.socket=toHandle;
	}
	public void run(){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			lines=new ArrayList<String>();
			String line=reader.readLine();
			System.out.println();
			while(!(line==null||line.isEmpty())){
				lines.add(line);
				line=reader.readLine();
			}
			response=new Response().setStatus(HTTPStatusCode.OK);
			if(lines.size()>0){
				request=new Request(lines);
				request.print();
			}
			String contentLength;
			if(request!=null&&request.getHttpMethod()==HTTPMethod.POST&&(contentLength=request.getHeaderParameterValue("Content-Length"))!=null){
				if(Integer.parseInt(contentLength)>0){
					line=reader.readLine();
					if(!line.isEmpty()){request.setPostQueryString(line);}
				}
			}
			if(request!=null&&request.getResourcePath().equals("/")){
				try{
					response.setFileContent("c:\\Users\\efokkema\\Desktop\\raytrace\\server\\index.html");
				}catch(FileNotFoundException e){
					response.setStatus(HTTPStatusCode.NotFound);
				}
				}else{response.setContent("something else...");}
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(response.toString());
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
class Request{
	private HTTPMethod method;
	private String resourcePath;
	private ArrayList<String[]> headerKeyValuePairs;
	private String postQueryString;
	private ArrayList<String> lines;
	public Request(ArrayList<String> lines){
		this.lines=lines;
		if(lines.size()>0){
			headerKeyValuePairs=new ArrayList<String[]>();
			String[] parts=lines.get(0).split("\\s");
			this.method=parts[0].equals("GET")?HTTPMethod.GET:HTTPMethod.POST;
			this.resourcePath=parts[1];
			for(int i=1;i<lines.size();i++){
				headerKeyValuePairs.add(lines.get(i).split(":\\s"));
			}
		}
	}
	public void print(){
		for(int i=0;i<this.lines.size();i++){
			System.out.println(this.lines.get(i));
		}
	}
	public HTTPMethod getHttpMethod(){
		return this.method;
	}
	public String getResourcePath(){
		return this.resourcePath;
	}
	public String getHeaderParameterValue(String key){
		String s=null;
		return s;
	}
	public void setPostQueryString(String s){
		this.postQueryString=s;
	}
}
class Response{
	private HTTPStatusCode status=HTTPStatusCode.OK;
	private String content="hoi";
	public Response setStatus(HTTPStatusCode status){
		this.status=status;
		return this;
	}
	private Calendar getDate(){
		return Calendar.getInstance();
	}
	public String toString(){
		ArrayList<String> lines=new ArrayList<String>();
		lines.add("HTTP/1.1 "+this.status.getCode()+" "+this.status.getDescription());
		lines.add("Date: "+new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", new Locale("en")).format(this.getDate().getTime()));
		lines.add("Server: Apache/2.0.63 (Unix) mod_ssl/2.0.63 OpenSSL/0.9.7e PHP/5.2.8");
		lines.add("Connection: close");
		lines.add("Content-Type: text/html; charset=UTF-8");
		lines.add("");
		lines.add(this.content);
		String s="";
		for(int i=0;i<lines.size();i++){
			s+=lines.get(i)+"\n";
		}
		return s;
	}
	public void setContent(String s){
		this.content=s;
	}
	public void setFileContent(String filePath) throws IOException{
		BufferedReader r=new BufferedReader(new FileReader(filePath));
		ArrayList<String> lines=new ArrayList<String>();
		String s;
		this.content="";
		while((s=r.readLine())!=null){
			this.content+=s+"\n";
		}
		r.close();
		//this.content=filePath;
	}
}
