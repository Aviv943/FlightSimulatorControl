package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import interpreter.AutoPilotParser;
import interpreter.CompParser;

import javax.swing.*;

public class Model extends Observable implements Observer {
    private SimulatorClient simulatorClient;
    public static volatile boolean stop = false;
    public static volatile boolean turn = true;
    public static volatile boolean head = false;
    private Interpreter interpreter;
    private static Socket socketPath;
    private  static PrintWriter outPath;
    private  static BufferedReader in;
    double startX;
    double startY;
    double planeX;
    double planeY;
    double markX;
    double markY;
    int[][] data;
    double offset;
    double currentlocationX;
    double currentlocationY;
    double currentHeading;
    ArrayList<String[]> intersections=new ArrayList<>();
    Thread route;
    Thread rudder;
    int indexPlan = 0;

    public Model() {
        simulatorClient = new SimulatorClient();
        interpreter = new Interpreter();
        route=new Thread(this::routeStart);
        rudder=new Thread(this::rudderStart);
    }

    public void GetPlane(double startX,double startY, double offset){
        this.offset=offset;
        this.startX=startX;
        this.startY=startY;
            new Thread(this::run).start();
        }

    public void parse(String[] script){
        interpreter.interpet(script);
    }

    public void execute() {
        interpreter.execute();
    }

    public void stopAutoPilot() {
        interpreter.stop();
    }

    public void connectPath(String ip,int port) {
        try {
            socketPath = new Socket(ip,port);
            outPath = new PrintWriter(socketPath.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socketPath.getInputStream()));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void connectManual(String ip,int port){
        simulatorClient.Connect(ip,port);
    }

    public void send(String[] data) {
        simulatorClient.Send(data);
    }

    public void findPath(int planeX,int planeY,int markX,int markY,int[][] data) {
        this.planeX=planeX;
        this.planeY=planeY;
        this.markX=markX;
        this.markY=markY;
        this.data=data;
        new Thread(()->{
                int j,i;
                System.out.println("\tSending problem to the server...");

                for (i = 0; i < data.length; i++) {
                    System.out.print("\t");
                    for (j = 0; j < data[i].length - 1; j++) {
                        outPath.print(data[i][j] + ",");
                    }
                    outPath.println(data[i][j]);
                }

                outPath.println("end");
                outPath.println(planeX+","+planeY);
                outPath.println(markX+","+markY);
                outPath.flush();
                String usol = null;

                try {
                    usol = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("\tSolution received");
                System.out.println(usol);
                String[]strings = usol.split(",");
                String[] notfiy=new String[strings.length+1];
                notfiy[0]="path";

                for(i=0;i<strings.length;i++) {
                    notfiy[i+1]=strings[i];
                }

                this.setChanged();
                this.notifyObservers(notfiy);
                this.buildFlyPlan(strings);

                if(!route.isAlive()) {
                    route.start();
                } else if(!Model.turn) {
                    route.interrupt();
                    route.start();
                }

        }).start();
    }

    private void rudderStart() {
        while(!head &&indexPlan<intersections.size()) {
            double heading,headingC;
            heading = Integer.parseInt(intersections.get(indexPlan)[0]);
            headingC=currentHeading;
            int degreeCom;
            int degree = (int) (heading - headingC);

            if (degree < 0) {
                degree += 360;
            }

            degreeCom = 360 - degree;
            double turning;

            if (degree < degreeCom) {
                turning = turnPlus(headingC);
            }
            else {
                turning = turnMinus(headingC);
            }

            double doubleTemp = (turning - headingC);

            if(doubleTemp>=340) {
                doubleTemp=360-doubleTemp;
            } else if(doubleTemp<-340)
                doubleTemp=-360-doubleTemp;

            if(Math.abs(heading-headingC)>9 && Math.abs(heading-headingC)<349) {
                CompParser.symbols.get("r").setV(doubleTemp/20);
                CompParser.symbols.get("e").setV(0.095);
            }
            else {
                CompParser.symbols.get("r").setV(doubleTemp / 100);
                CompParser.symbols.get("e").setV(0.053);
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void routeStart()
    {
        while(turn) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        rudder.start();
        double intersectionX,intersectionY,pathX,pathY,endX,endY;
        pathX=startX+(planeY-1)*offset;
        pathY=startY-(planeX-1)*offset;
        endX=startX+(markY-1)*offset;
        endY=startY-(markX-1)*offset;
        int radiusX=17;

        while(!turn && indexPlan<intersections.size()) {
            int h = Integer.parseInt(intersections.get(indexPlan)[0]);
            int n = Integer.parseInt(intersections.get(indexPlan)[1]);
            switch (h) {
                case 360:
                    intersectionX = pathX;
                    intersectionY = pathY + (n - 1) * offset;
                    break;
                case 45:
                    intersectionX = pathX + (n - 1) * offset;
                    intersectionY = pathY + (n - 1) * offset;
                    break;
                case 90:
                    intersectionX = pathX + (n - 1) * offset;
                    intersectionY = pathY;
                    break;
                case 135:
                    intersectionX = pathX + (n - 1) * offset;
                    intersectionY = pathY - (n - 1) * offset;
                    break;
                case 180:
                    intersectionX = pathX;
                    intersectionY = pathY - (n - 1) * offset;
                    ;
                    break;
                case 225:
                    intersectionX = pathX - (n - 1) * offset;
                    intersectionY = pathY - (n - 1) * offset;
                    break;
                case 270:
                    intersectionX = pathX - (n - 1) * offset;
                    intersectionY = pathY;
                    break;
                case 315:
                    intersectionX = pathX - (n - 1) * offset;
                    intersectionY = pathY + (n - 1) * offset;
                    break;
                default:
                    intersectionX = 0;
                    intersectionY = 0;
            }
            if(indexPlan==intersections.size()-1)
            {
                intersectionX=endX;
                intersectionY=endY;
            }
            while (Math.abs(currentlocationX - intersectionX) >radiusX * offset || Math.abs(currentlocationY - intersectionY) > radiusX * offset) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            indexPlan++;
            pathX=currentlocationX;
            pathY=currentlocationY;

        }
        CompParser.symbols.get("goal").setV(1);

    }
    private void buildFlyPlan(String[] solution)
    {
        intersections=new ArrayList<>();
        int count=0;

        for(int i=0;i<solution.length-1;i++) {
            if(solution[i].equals(solution[i+1])) {
                count++;
            }
            else {
                String[] tmp = new String[2];
                tmp[0] = solution[i];
                tmp[1] = count + 1 + "";
                intersections.add(tmp);
                count=0;
            }
        }

        if(count!=0) {
            String[] strings = new String[2];
            strings[0] = solution[solution.length-1];
            strings[1]=count+1+"";
            intersections.add(strings);
        }

        for(int i=0;i<intersections.size();i++) {
            int tmp=Integer.parseInt(intersections.get(i)[1]);
            if(tmp<=5) {
                int index;
                int tmp2;

                if(i!=0) {
                    index=i-1;
                }
                else
                {
                    index=i+1;
                }

                tmp2 = Integer.parseInt(intersections.get(index)[1]);
                tmp2+=tmp;

                String[] strings=new String[]{intersections.get(index)[0],tmp2+""};
                intersections.set(index,strings);
                intersections.remove(i);
            }
        }

        for(int i=0;i<intersections.size()-1;i++) {
            if(intersections.get(i)[0].equals(intersections.get(i+1)[0])) {
                int tmp=Integer.parseInt(intersections.get(i)[1])+Integer.parseInt(intersections.get(i+1)[1]);
                String s=""+tmp;
                intersections.get(i)[1]=s;
                intersections.remove(i+1);
            }
        }

        for(int i=0;i<intersections.size();i++) {
            int tmp=Integer.parseInt(intersections.get(i)[1]);
            String direct=intersections.get(i)[0];
            int degree=clacDegree(direct);

            if(tmp<=15&&tmp>5) {
                if(i+1<intersections.size())
                {
                    if(degree!=360 && degree!=90) {
                        degree += degree < clacDegree(intersections.get(i + 1)[0]) ? 45 : -45;
                    }
                    else if(degree==360){
                        if(clacDegree(intersections.get(i + 1)[0])==90) {
                            degree=45;
                        } else {
                            degree-=45;
                        }
                    }
                    else if(degree==90) {
                        if(clacDegree(intersections.get(i + 1)[0])==360) {
                            degree=45;
                        } else {
                            degree-=45;
                        }
                    }
                }
            }

            String s;
            s=degree+"";
            intersections.get(i)[0]=s.intern();
        }
    }

    private int clacDegree(String s) {
        int degree=0;
        switch (s){
            case "Down":
                degree=180;
                break;
            case "Right":
                degree=90;
                break;
            case "Left":
                degree=270;
                break;
            case "Up":
                degree=360;
        }

        return degree;
    }

    private void run() {
        Socket socket = null;

        try {
            socket = new Socket("127.0.0.1", 5402);
            System.out.println("Connected to the server");
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (!stop) {
                printWriter.println("dump /position");
                printWriter.flush();
                String line;
                ArrayList<String> lines = new ArrayList<>();

                while (!(line = bufferedReader.readLine()).equals("</PropertyList>")) {
                    if (!line.equals(""))
                        lines.add(line);
                }

                String longtitude = lines.get(2);
                String latitude = lines.get(3);
                String[] x = longtitude.split("[<>]");
                String[] y = latitude.split("[<>]");
                bufferedReader.readLine();
                printWriter.println("get /instrumentation/heading-indicator/indicated-heading-deg");
                printWriter.flush();
                String[] h = bufferedReader.readLine().split(" ");
                int tmp = h[3].length();
                currentlocationX = Double.parseDouble(x[2]);
                currentlocationY = Double.parseDouble(y[2]);
                currentHeading = Double.parseDouble(h[3].substring(1, tmp - 1));
                String[] data = {"plane", x[2], y[2], h[3].substring(1, tmp - 1)};
                this.setChanged();
                this.notifyObservers(data);

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            socket.close();
        } catch (IOException exception) {
            if (exception instanceof ConnectException) {
                JOptionPane.showMessageDialog(null, "Server is not connected", "Connection Error", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                exception.printStackTrace();
            }

            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public interface Turn {
     double Do(double x);
    }

    public void makeTurn(Turn t,double heading,double currentHeading)
    {
        double minus=Math.abs(heading-currentHeading);
        double h=currentHeading;
        while(minus>30 && minus < 335 &&!Model.turn) {
            double tmp=t.Do(h);
            CompParser.symbols.get("hroute").setV(tmp);

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            minus=Math.abs(heading-tmp);
            h=tmp;
        }

        CompParser.symbols.get("hroute").setV(heading);
    }

    public double turnPlus(double currentHeading) {
        int tmp=(int)currentHeading+7;

        if(tmp>360) {
            tmp-=360;
        }

        return tmp;
    }

    public double turnMinus(double currentHeading) {
        int tempInt=(int)currentHeading-7;

        if(tempInt<0) {
            tempInt=360+tempInt;
        }

        return  tempInt;
    }

    public void stopAll()
    {
        Model.stop=true;

        if (outPath!=null) {
            outPath.close();
        }

        try {
            if (in!=null) {
                in.close();
            }
            if (socketPath!=null) {
                socketPath.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        simulatorClient.stop();
        AutoPilotParser.calc_thread.interrupt();
        AutoPilotParser.close_it =true;
        Model.turn=true;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
