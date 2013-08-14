import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.ArrayList;

public class FinalProject
{
	public static void main(String[] args)  throws FileNotFoundException{
		Scanner in=new Scanner(System.in);
		boolean notQuit=true;
    Client client=new Client();
		while(notQuit)
		{
		System.out.println("Please type 1 if you want to generate a surface, type 2 if you want to scan the surface with a tip, type 3 if you want to process the image, type 0 to quit.");
		int choice=in.nextInt();
		switch (choice) 
		{
			case 1:
			System.out.println("What is the lowest value of the surface? Please specify an integer.");
			int min=in.nextInt();
			System.out.println("What is the highest value of the surface? Please specify an integer.");
			int max=in.nextInt();
			System.out.println("Please specify the name of your generated file. Please avoid '_' in your name.");
			String fileName=in.next();
            System.out.println("How many points do you want to get on this surface?");
            int pointNo=in.nextInt();
			File fileOne=client.generateSurface(min, max, fileName, pointNo);
			break;
			case 2:
			System.out.println("What is the surface source file? Please specify the name of the file without the '.txt'.");
			String fileNameToScan=in.next();
			System.out.println("What is the strategy you want to use for the feedback system. Type 1 for linear, 2 for elastic, 3 for sine.");
			int strategy=in.nextInt();
			System.out.println("Please specify the distance between your tip and surface.");
			int distance=in.nextInt();
			System.out.println("Please specify the your scanning speed.");
			int speed=in.nextInt();
			File fileTwo=client.scanningSurface(fileNameToScan, strategy, distance, speed);
			break;
      case 3:
      System.out.println("What is the surface source file? Please specify the name of the file without the '.txt'.");
      String fileNameToProcess=in.next();
      System.out.println("What is the tool you want to use to process the image. Type 4 for balance, 5 for smooth. Remember smooth will only be effective when the image is obtained by speed=1");
      int strategyToProcess=in.nextInt();
      File fileThree=client.processImage(fileNameToProcess, strategyToProcess);
      break;
      case 0:
      notQuit=false;
      break;

			default:
				System.out.println("Sorry. I cannot understand this.");
			break;

			
		  }
     }
	}
}
class Client
{
    public File generateSurface(int min, int max, String fileName, int pointNo) throws FileNotFoundException
	{
       return this.clientProxy.generateSurface(min,max,fileName,pointNo);
	}

	public File scanningSurface(String fileName, int strategy, int distance, int speed) throws FileNotFoundException
	{
		return this.clientProxy.scanningSurface(fileName, strategy, distance, speed);
	}

  public File processImage(String fileName, int strategy) throws FileNotFoundException
  {
    return this.clientProxy.processImage(fileName, strategy);
  }
	private ClientProxy clientProxy=new ClientProxy();
}
class ClientProxy
{
     public File generateSurface(int min, int max, String fileName, int pointNo) throws FileNotFoundException
	 {    ArrayList<Integer> surfaceArray=new ArrayList<Integer>();
	 	    CallMessage message=new CallMessage(min,max,fileName,pointNo,surfaceArray,1,1,0,0);//type, strategy, distance, speed
        CallMessage messageBack= brokerClient.process(message);
        return messageBack.writeToFile();
	 }

	 public File scanningSurface(String fileName, int strategy, int distance, int speed) throws FileNotFoundException
	 {
	 	Scanner inTheFile=new Scanner(new File(fileName+".txt"));
	 	ArrayList<Integer> surfaceArray=new ArrayList<Integer>();
	 	int min=Integer.parseInt(inTheFile.next());
	 	int max=Integer.parseInt(inTheFile.next());
    int pointNo=Integer.parseInt(inTheFile.next());
	 	while(inTheFile.hasNext())
	 	{
	 		surfaceArray.add(Integer.parseInt(inTheFile.next()));
	 	}
	 	CallMessage message=new CallMessage(min, max, fileName, pointNo, surfaceArray, 2, strategy, distance, speed);
	 	CallMessage messageBack=brokerClient.process(message);
	 	return messageBack.writeToFile();
	 }

   public File processImage(String fileName, int strategy) throws FileNotFoundException
   {
    Scanner inTheImageFile=new Scanner(new File(fileName+".txt"));
    ArrayList<Integer> surfaceArray=new ArrayList<Integer>();
    int min=Integer.parseInt(inTheImageFile.next());
    int max=Integer.parseInt(inTheImageFile.next());
    int pointNo=Integer.parseInt(inTheImageFile.next());
    while(inTheImageFile.hasNext())
    {
      surfaceArray.add(Integer.parseInt(inTheImageFile.next()));
    }
    CallMessage message=new CallMessage(min, max, fileName, pointNo, surfaceArray, 3, strategy, 0, 0);
    CallMessage messageBack=brokerClient.process(message);
    return messageBack.writeToFile();
   }

	 private BrokerClient brokerClient=new BrokerClient();
}
class CallMessage
{
    public CallMessage(int min, int max, String fileName, int pointNo, ArrayList<Integer> surfaceArray, int type, int strategy,int distance, int speed)
	{
         this.min=min;
         this.max=max;
         this.fileName=fileName;
         this.pointNo=pointNo;
         this.surfaceArray=surfaceArray;
         this.type=type;
         this.strategy=strategy;
         this.distance=distance;
         this.speed=speed;
	}

	public CallMessage(String string)
	{
		String[] tempArray=string.split("_");
		this.min=Integer.parseInt(tempArray[0]);
		this.max=Integer.parseInt(tempArray[1]);
		this.fileName=tempArray[2];
    this.pointNo=Integer.parseInt(tempArray[3]);
    this.type=Integer.parseInt(tempArray[4]);
    this.strategy=Integer.parseInt(tempArray[5]);
    this.distance=Integer.parseInt(tempArray[6]);
    this.speed=Integer.parseInt(tempArray[7]);
    int arrayLength=tempArray.length;
    for(int i=0; i<arrayLength-8;i++)
      this.surfaceArray.add(Integer.parseInt(tempArray[8+i]));

	}

	public File writeToFile() throws FileNotFoundException
	{
         PrintWriter writer = new PrintWriter(this.fileName+".txt");
         writer.println(this.min);
         writer.println(this.max);
         writer.println(this.pointNo);
         SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(surfaceArray,1);
         for (iterator.first();!iterator.isDone();iterator.next())
         writer.println(iterator.currItem());
         writer.close();
         File file=new File(this.fileName+".txt");
         return file;
	}

	public int getMin()
	{
		return this.min;
	}
	public int getMax()
	{
		return this.max;
	}
	public String getFileName()
	{
		return this.fileName;
	}

  public int getPointNo()
  {
    return this.pointNo;
  }
	public ArrayList<Integer> getSurfaceArray()
	{
		return this.surfaceArray;
	}
	public int getType()
	{
		return this.type;
	}
	public int getStrategy()
	{
		return this.strategy;
	}
	public int getDistance()
	{
		return this.distance;
	}
	public int getSpeed()
	{
		return this.speed;
	}
	private int min;
	private int max;
	private String fileName;
  private int pointNo;
	private ArrayList<Integer> surfaceArray=new ArrayList<Integer>();
	private int type;
	private int strategy;
	private int distance;
	private int speed;
}
class BrokerClient
{
    public CallMessage process(CallMessage message)
	{
        String string=message.getMin()+"_"+message.getMax()+"_"+message.getFileName()+"_"+message.getPointNo()+"_"+message.getType()+"_"+message.getStrategy()+"_"+message.getDistance()+"_"+message.getSpeed();
        SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(message.getSurfaceArray(),1);
        for (iterator.first();!iterator.isDone();iterator.next())
        {
        	string=string+"_"+iterator.currItem();
        }
        
        String stringBack=this.transport.manipulate(string);
        CallMessage messageBack=new CallMessage(stringBack);
        return messageBack;
	}
	private Transport transport=new Transport();
}
class Transport
{
	public String manipulate(String string)
	{
        return this.brokerServer.process(string);
	}
	private BrokerServer brokerServer=new BrokerServer();
}
class BrokerServer
{
    public String process(String string)
    {
    	CallMessage message=new CallMessage(string);
    	CallMessage messageBack=this.serverProxy.unpacking(message);
    	String stringBack=messageBack.getMin()+"_"+messageBack.getMax()+"_"+messageBack.getFileName()+"_"+messageBack.getPointNo()+"_"+messageBack.getType()+"_"+messageBack.getStrategy()+"_"+messageBack.getDistance()+"_"+messageBack.getSpeed();
      SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(messageBack.getSurfaceArray(), 1);
    	for (iterator.first();!iterator.isDone();iterator.next())
        {
        	stringBack=stringBack+"_"+iterator.currItem();
        }

        return stringBack;
    }
    private ServerProxy serverProxy= new ServerProxy();
}
class ServerProxy
{
    public CallMessage unpacking(CallMessage message)
	{
		server=Server.getServer();
    if (message.getType()==1)
		{
             ArrayList<Integer> result=new ArrayList<Integer>();
             result=this.server.generateSurface(message.getMin(), message.getMax(), message.getPointNo());
             CallMessage messageBack=new CallMessage(message.getMin(),message.getMax(),message.getFileName(),message.getPointNo(),result, 1, 1, 0, 0);
             return messageBack;
		}
		else if (message.getType()==2)
		{
             ArrayList<Integer> result=new ArrayList<Integer>();
             result=this.server.scanningSurface(message.getSurfaceArray(),message.getStrategy(),message.getDistance(),message.getSpeed());
             CallMessage messageBack=new CallMessage(message.getMin(),message.getMax(),message.getFileName()+".Mode"+message.getStrategy()+"Distance"+message.getDistance()+"Speed"+message.getSpeed(), message.getPointNo(), result, 2, message.getStrategy(),message.getDistance(),message.getSpeed() );
             return messageBack;
		}
		else
		{
			       ArrayList<Integer> result=new ArrayList<Integer>();
             result=this.server.processImage(message.getSurfaceArray(),message.getStrategy());
             CallMessage messageBack=new CallMessage(message.getMin(),message.getMax(),message.getFileName()+".ProcessMode"+message.getStrategy(), message.getPointNo(), result, 3, message.getStrategy(),0,0 );
             return messageBack;
		}
		     
	}

    private Server server;
}
class Server
{
	private Server()
  {

  };
  public ArrayList<Integer> generateSurface(int min, int max, int pointNo)
	{
		ArrayList<Integer> result=new ArrayList<Integer>();
		Random randomGenerator = new Random();
		for (int i=0;i<pointNo;i++)
		{
      result.add(min+randomGenerator.nextInt(max-min+1));
		}
		return result;
	}

	public ArrayList<Integer> scanningSurface(ArrayList<Integer> surfaceArray, int strategy, int distance, int speed)
	{
		Context context;
		if(strategy==1)
		{
			context=new Context(new Linear());
            return context.executeStrategy(surfaceArray, distance, speed);
		}
		else if(strategy==2)
		{
			context=new Context(new Elastic());
            return context.executeStrategy(surfaceArray, distance, speed);
		}
		else
		{
			context=new Context(new Sine());
            return context.executeStrategy(surfaceArray, distance, speed);
		}
	}

  public ArrayList<Integer> processImage(ArrayList<Integer> surfaceArray, int strategy)
  {
    ContextToProcess context;
    if(strategy==4)
    {
      context=new ContextToProcess(new Balance());
            return context.executeStrategy(surfaceArray);
    }
    else
    {
      context=new ContextToProcess(new Smooth());
            return context.executeStrategy(surfaceArray);
    }
  }

  public static Server getServer()
  {
    if (server==null)
    {
      server=new Server();

    }
    return server;
  }

  private static Server server=null;

}
class Context 
  {
    private Strategy strategy;

    public Context(Strategy strategy) {
      this.strategy = strategy;
    }

    public ArrayList<Integer> executeStrategy(ArrayList<Integer> surfaceArray,int distance, int speed) 
    {
        ArrayList<Integer> originalArray=new ArrayList<Integer>();
        ArrayList<Integer> realArray=new ArrayList<Integer>();
        
        SurfaceIterator<Integer> iteratorSur=new SurfaceIterator<Integer>(surfaceArray,1);
        int pointNo=surfaceArray.size();
        originalArray=this.strategy.execute(surfaceArray, distance, speed);
        SurfaceIterator<Integer> iteratorOrg=new SurfaceIterator<Integer>(originalArray,1);
        boolean tipOK=true;
        iteratorOrg.first();
        iteratorSur.first();
        while((!iteratorSur.isDone())&&tipOK)
        {
          if (iteratorOrg.currItem()>=iteratorSur.currItem())
            realArray.add(iteratorOrg.currItem());
          else
            {realArray.add(iteratorSur.currItem());
              tipOK=false;
              System.out.println("The tip is broken.");
            }
           iteratorOrg.next();
           iteratorSur.next();
        }
        int lastNumber=realArray.get(realArray.size()-1);
        int realArraySize=realArray.size();
        for(int i=0; i<pointNo-realArraySize;i++)
        {
          realArray.add(lastNumber);
        }
        return realArray;
        
    }
  }
interface Strategy 
  {
    ArrayList<Integer> execute(ArrayList<Integer> surfaceArray, int distance, int speed) ; 
  }

class Linear implements Strategy
//In the linear mode, for each scanning point, the distance between the tip and surface is measured and by comparing with the specified value, the difference determines how the tip will move but the maximum movement is 1000
{
    public ArrayList<Integer> execute(ArrayList<Integer> surfaceArray, int distance, int speed)
    {
       ArrayList<Integer> result=new ArrayList<Integer>();
       int pointNo=surfaceArray.size();
       SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(surfaceArray,speed);
       iterator.first();
       result.add(iterator.currItem()+distance);
       for(iterator.first();!iterator.isDoneWithSpeed();iterator.next())
       {  int lastNumber=result.get(result.size()-1);//return the last number of the current arraylist
          for (int j=1; j<=speed;j++)	
          {
          	if (lastNumber-iterator.currItem()-distance<=-1000)
                result.add(lastNumber+1000);
            else if (lastNumber-iterator.currItem()-distance>=1000)
            	  result.add(lastNumber-1000);
            else
          		  result.add(iterator.currItem()+distance);
          }
       
       }
       int group=(pointNo-1)/speed;
       int index=speed*group+1;
       int lastNumber=result.get(result.size()-1);
       for (int j=index; j<pointNo;j++)	
          {
          	if (lastNumber-iterator.currItem() -distance<=-1000)
                result.add(lastNumber+1000);
            else if (lastNumber-iterator.currItem()-distance>=1000)
            	  result.add(lastNumber-1000);
            else
          		  result.add(iterator.currItem()+distance);
          }
        return result;

    }
}
class Elastic implements Strategy
//In the elastic mode, when the distance between the tip and the surface has a small bias from the specified value, the restoring is slow; when the bias is big, the restoring is fast
{
    public ArrayList<Integer> execute(ArrayList<Integer> surfaceArray, int distance, int speed)
    {
       ArrayList<Integer> result=new ArrayList<Integer>();
       int pointNo=surfaceArray.size();
       SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(surfaceArray,speed);
       iterator.first();
       result.add(iterator.currItem()+distance);
        for(iterator.first();!iterator.isDoneWithSpeed();iterator.next())
       {  int lastNumber=result.get(result.size()-1);//return the last number of the current arraylist
          for (int j=1; j<=speed;j++) 
          {
            if (lastNumber-iterator.currItem()-distance<=-1000||lastNumber-iterator.currItem()-distance>=1000)
                result.add(lastNumber-(lastNumber-iterator.currItem()-distance));
            else if (lastNumber-iterator.currItem()-distance<=-800||lastNumber-iterator.currItem()-distance>=800)
                result.add(lastNumber-(int)((lastNumber-iterator.currItem()-distance)*0.8));
            else if (lastNumber-iterator.currItem()-distance<=-500||lastNumber-iterator.currItem()-distance>=500)
                result.add(lastNumber-(int)((lastNumber-iterator.currItem()-distance)*0.5));
            else
                result.add(lastNumber-(int)((lastNumber-iterator.currItem()-distance)*0.3));
          }
       
       }
      
       int group=(pointNo-1)/speed;
       int index=speed*group+1;
       int lastNumber=result.get(result.size()-1);
       for (int j=index; j<pointNo;j++) 
          {
            if (lastNumber-iterator.currItem()-distance<=-1000||lastNumber-iterator.currItem()-distance>=1000)
                result.add(lastNumber-(lastNumber-iterator.currItem()-distance));
            else if (lastNumber-iterator.currItem()-distance<=-800||lastNumber-iterator.currItem()-distance>=800)
                result.add(lastNumber-(int)((lastNumber-iterator.currItem()-distance)*0.8));
            else if (lastNumber-iterator.currItem()-distance<=-500||lastNumber-iterator.currItem()-distance>=500)
                result.add(lastNumber-(int)((lastNumber-iterator.currItem()-distance)*0.5));
            else
                result.add(lastNumber-(int)((lastNumber-iterator.currItem()-distance)*0.3));
          }

        return result;

    }

}
class Sine implements Strategy
//in the sine mode, the restoring of the tip is based on the sine function
{
    public ArrayList<Integer> execute(ArrayList<Integer> surfaceArray, int distance, int speed)
    {
       ArrayList<Integer> result=new ArrayList<Integer>();
       int pointNo=surfaceArray.size();
       SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(surfaceArray,speed);
       iterator.first();
       result.add(iterator.currItem()+distance);
        for(iterator.first();!iterator.isDoneWithSpeed();iterator.next())
       {  int lastNumber=result.get(result.size()-1);//return the last number of the current arraylist
          for (int j=1; j<=speed;j++) 
          {
            if (lastNumber-iterator.currItem()-distance<=-800)
                result.add(lastNumber+800);
            else if (lastNumber-iterator.currItem()-distance>=800)
                result.add(lastNumber-800);
            else
                result.add(lastNumber-(int)(800*Math.sin((lastNumber-iterator.currItem()-distance)/1600.0*Math.PI)));
          }
       
       }
       int group=(pointNo-1)/speed;
       int index=speed*group+1;
       int lastNumber=result.get(result.size()-1);
       for (int j=index; j<pointNo;j++) 
          {
            if (lastNumber-iterator.currItem()-distance<=-800)
                result.add(lastNumber+800);
            else if (lastNumber-iterator.currItem()-distance>=800)
                result.add(lastNumber-800);
            else
                result.add(lastNumber-(int)(800*Math.sin((lastNumber-iterator.currItem()-distance)/1600.0*Math.PI)));
          }
      
        return result;

    }

}

class ContextToProcess
  {
    private StrategyToProcess strategy;

    public ContextToProcess(StrategyToProcess strategy) {
      this.strategy = strategy;
    }

    public ArrayList<Integer> executeStrategy(ArrayList<Integer> surfaceArray) 
    {
      return this.strategy.execute(surfaceArray);
    }
  }
interface StrategyToProcess
  {
    ArrayList<Integer> execute(ArrayList<Integer> surfaceArray) ; 
  }
class Balance implements StrategyToProcess
{
    public ArrayList<Integer> execute(ArrayList<Integer> surfaceArray)
    {
       ArrayList<Integer> result=new ArrayList<Integer>();
       int pointNo=surfaceArray.size();
       double diff=(-surfaceArray.get(0)+surfaceArray.get(pointNo-1))/(double)(pointNo-1);
       SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(surfaceArray,1);
       for(iterator.first();!iterator.isDone();iterator.next())
       {
        result.add(iterator.currItem()-(int)(diff*iterator.getIndex()));
       }
       return result;
    }
}

class Smooth implements StrategyToProcess
{
    public ArrayList<Integer> execute(ArrayList<Integer> surfaceArray)
    {
       ArrayList<Integer> result=new ArrayList<Integer>();
       int pointNo=surfaceArray.size();
       SurfaceIterator<Integer> iterator=new SurfaceIterator<Integer>(surfaceArray,1);
       for(iterator.first();!iterator.isDone();iterator.next())
       {
          result.add(iterator.currItem());
       }

       for(int i=0;i<pointNo-2;i++)
       {
          if (result.get(i)-result.get(i+1)>1500&&result.get(i+2)-result.get(i+1)>1500)
            result.set(i+1, (result.get(i)+result.get(i+2))/2-1500);
          if (result.get(i)-result.get(i+1)<-1500&&result.get(i+2)-result.get(i+1)<-1500)
            result.set(i+1, (result.get(i)+result.get(i+2))/2+1500);
       }
       return result;
    }
}


class SurfaceIterator<Integer>
{
  public SurfaceIterator(ArrayList<Integer> surfaceArray, int speed)
  {
    this.surfaceArray=surfaceArray;
    this.speed=speed;
  }
  
  public void first()
    {
      this.currentIndex=0;
    }
  public void next()
  {
    this.currentIndex+=speed;
  }
  public void previous()
  {
    this.currentIndex-=speed;
  }
  public boolean isDone()
  {
    return this.currentIndex >=this.surfaceArray.size();
  }
  public boolean isDoneWithSpeed()
  {
    return this.currentIndex >=this.surfaceArray.size()-speed;
  }

  public Integer currItem()
  {
    return this.surfaceArray.get(currentIndex);
  }

  public int getIndex()
  {
    return this.currentIndex;
  }
  
  private int currentIndex;
  private ArrayList<Integer> surfaceArray=new ArrayList<Integer>();
  private int speed;
}

