import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by Keenan on 2017-04-04.
 */
public aspect DisplayCells {

    pointcut updateCells(): (call(* ConwaysGameOfLife.updateSpace(..)));
    pointcut start(): (call(* ConwaysGameOfLife.simulate(..)));

    JFrame frame = new JFrame("Conway's Game of Life");
    JLabel label = new JLabel("Conway's Game of Life", SwingConstants.CENTER);


    //Init Jframe
    before(): start(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(label);
        frame.setBounds(0,0,1000,1000);

        label.setFont(new Font("Courier New", Font.PLAIN,6));

        frame.setSize(500,800);
        frame.setVisible(true);
    }

    after() returning(int [][]array): updateCells(){
        label.setText(array2DtoString(array));
    }

    private String array2DtoString(int [][]arr)
    {
        String retString = "<html>";
        for(int i =0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                if(arr[i][j] == 0)
                    retString += '░';
                else
                    retString += '█';
            }
            retString += "<br>";
        }
        retString += "</html>";
        return retString;
    }
}


