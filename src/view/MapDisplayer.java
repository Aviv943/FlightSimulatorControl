package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;



public class MapDisplayer extends Canvas {
    int[][] mapData;
    double min = Double.MAX_VALUE;
    double max=0;

    public void setMapData(int[][] mapData) {
        this.mapData = mapData;

        for(int i=0;i<mapData.length;i++) {
            for (int j=0;j<mapData[i].length;j++)
            {
                if(min>mapData[i][j]) {
                    min=mapData[i][j];
                }

                if(max<mapData[i][j]) {
                    max=mapData[i][j];
                }
            }
        }

        double newMax = 255;
        double newMin = 0;

        for (int i=0;i<mapData.length;i++) {
            for (int j=0;j<mapData[i].length;j++) {
                mapData[i][j] = (int)((mapData[i][j] - min) / (max - min) * (newMax - newMin) + newMin);
            }
        }

        redraw();
    }

    public void redraw() {
        if (mapData!=null) {
            double height = getHeight();
            double width = getWidth();
            double calculatedHeight = height/mapData.length;
            double calculatedWidth = width/mapData[0].length;
            GraphicsContext graphicsContext = getGraphicsContext2D();

            for (int i=0; i<mapData.length; i++) {
                for (int j = 0; j < mapData[i].length; j++) {
                    int data = mapData[i][j];
                    graphicsContext.setFill(Color.rgb(255-data,0+data,0));
                    graphicsContext.fillRect(j * calculatedWidth,i * calculatedHeight, calculatedWidth, calculatedHeight);
                }
            }
        }
    }
}