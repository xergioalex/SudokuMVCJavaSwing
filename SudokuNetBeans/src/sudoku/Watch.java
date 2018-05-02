/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//@author XergioAleX

/**
 *  Clase Vista que representa por medio  de interfase Grafica, un sudoku
 *  con estructura defenida en la clase Model, usando graficos de Java Swing y Awt
 */
public class Watch extends JFrame{
    /** Modelo del sudoku representado en la interfase grafica*/
    private Model sudoku;
    /** Matriz de Paneles que representan cada celda del sudoku 9x9*/
    private JPanel[][] board;

    //Botones
    /** Boton cuyo evento resuelve el sudoku con el algoritmo Bactracking*/
    private JButton solve;
    /** Boton cuyo evento deshace una asignacion de valores dentro del sudoku*/
    private JButton undo;
    /** Boton cuyo evento rehace una asignacion de valores dentro del sudoku*/
    private JButton redo;
    /** Boton cuyo evento vacia las pilas de deshacer y rehacer*/
    private JButton freeze;

    //Etiquetas
    /**Etiqueta contenida dentro de cada panel del sudoku que representa un valor ya asignado al sudoku*/
    private JLabel text;
    /**Matriz de etiquetas contenidas dentro de cada panel del sudoku, que representan
      los posibles Candidatos*/
    private JLabel[][] cands;


    //Pilas para deshacer y rehacer
    /** Pila que guarda cada estado del sudoku, para poder volver a un estado anterior*/
    private Stack<Model> undos = new Stack<Model>();
    /** Pila que guarda cada estado del sudoku deshecho por undo, para volver al paso siguiente*/
    private Stack<Model> redos = new Stack<Model>() ;

    /**
     * Crea un nuevo objeto tipo Model que representa un sudoku, y construye la
     * interfase Grafica, del Modelo (Sudoku).
     */
    public Watch(File archivo) throws FileNotFoundException, IOException{
        sudoku= new Model(archivo);
        initComponents();
    }

    /** Se encarga de Construir la interfase grafica del Sudoku, esta consta de
     *  una matriz de paneles 9x9 que representa el tablero sudoku, y cada panel
     *  contiene una lista de Etiquetas (JLabel) que representan el valor y los
     *  candidatos de la celda.
     */
    public void initComponents(){

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); //Finaliza la ejecucion "Cerrar"
        this.setTitle("Sudoku by Xergio"); //Titulo de la ventana
        this.setSize(546, 640); //Tamano de la Ventana
        this.setLayout(null); //Borrar layout
        this.setLocationRelativeTo(null); //Centro la ventana
        this.setResizable(false);

        /*---------------------------------------------------------------------------------------------*/
        board= new JPanel[9][9]; //Defino un tablero de 9x9 paneles
        int value; //Temporal para obtener valor de la celda
        final String[] shape= sudoku.getShape(); //Obtengo la forma del sudoku

        //Ciclo que inicializa todo el tablero con sus respectivos valores y listas de candidatos
        for( int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j]=new JPanel();
                board[i][j].setBounds(60*j,60*i,60,60);
                board[i][j].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                board[i][j].setLayout(null);

                text = new JLabel();
                text.setFont(new Font("forte",9,40));
                text.setForeground(Color.black);
                board[i][j].add(text);
                cands= new JLabel[3][3];
                for (int k = 0; k < 3; k++) {

                    for (int l = 0; l < 3; l++) {
                        int numbCand=k*3+l;
                        cands[k][l]= new JLabel(""+numbCand);
                        cands[k][l].setFont(new Font("forte",9,15));
                        cands[k][l].setForeground(Color.black);
                        board[i][j].add(cands[k][l]);
                    }

                }

                //Se colorea el tablero en base a la forma
                switch(shape[i].charAt(j)){
                    case ('1'): board[i][j].setBackground(new Color(238,248,255)); break;
                    case ('2'): board[i][j].setBackground(new Color(250,255,0));   break;
                    case ('3'): board[i][j].setBackground(new Color(198,141,141)); break;
                    case ('4'): board[i][j].setBackground(new Color(239,233,133)); break;
                    case ('5'): board[i][j].setBackground(new Color(255,252,201)); break;
                    case ('6'): board[i][j].setBackground(new Color(160,217,232)); break;
                    case ('7'): board[i][j].setBackground(new Color(91,246,143));  break;
                    case ('8'): board[i][j].setBackground(new Color(255,157,112)); break;
                    case ('9'): board[i][j].setBackground(new Color(255,227,224)); break;
                }
                value=sudoku.getCellValue(i, j);
                if(value!=0){
                    final int x=i;
                    final int y=j;
                    text.setText(""+value);
                    text.setBounds(20, 10, 40,40);
                }
                else{
                    for(int cand:sudoku.getCellValues(i, j)){
                        final int x=i;
                        final int y=j;
                        final int numb = cand;
                        cands[(cand-1)/3][(cand-1)%3].setText(""+cand);
                        cands[(cand-1)/3][(cand-1)%3].setBounds(10+16*((cand-1)%3),8+16*((cand-1)/3),15,15);
                        cands[(cand-1)/3][(cand-1)%3].addMouseListener(new MouseListener(){
                            public void mouseClicked(MouseEvent e) {
                                undos.add(new Model(sudoku));
                                redos.clear();
                                undo.setEnabled(true);
                                freeze.setEnabled(true);
                                redo.setEnabled(false);
                                sudoku.setCellValue(x, y, numb);
                                sudoku.test(x, y);
                                update();
                                repaint();
                                if(sudoku.isSolve())
                                    JOptionPane.showMessageDialog(null, "Felicidades ha resuelto el sudoku!!!","Notificacion de Exito", JOptionPane.INFORMATION_MESSAGE);
                            }
                            public void mousePressed(MouseEvent e) {}
                            public void mouseReleased(MouseEvent e) {}
                            public void mouseEntered(MouseEvent e) {
                                JLabel label=(JLabel) board[x][y].getComponent(numb);
                                label.setFont(new Font("forte",9,16));
                                label.setForeground(Color.blue);
                                label.setText(""+numb);
                                label.setBounds(10+16*((numb-1)%3),8+16*((numb-1)/3),15,15);
                                repaint();
                            }
                            public void mouseExited(MouseEvent e) {
                                JLabel label=(JLabel) board[x][y].getComponent(numb);
                                label.setFont(new Font("forte",9,15));
                                label.setForeground(Color.black);
                                label.setText(""+numb);
                                label.setBounds(10+16*((numb-1)%3),8+16*((numb-1)/3),15,15);
                                repaint();
                            }
                        });
                    }
                }
                this.add(board[i][j]);
            }
        }
        /*---------------------------------------------------------------------------------------------*/

        //ACCIONES, BOTONES

        //RESUELVE EL SUDOKU
        solve= new JButton("Solve");
        solve.setFont(new Font("forte",9,16));
        solve.setBounds(390,550, 100, 50);
        solve.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent action){
               redos.clear();
               undos.add(new Model(sudoku));
               boolean resuelto=sudoku.backtracking();
               undo.setEnabled(true);
               redo.setEnabled(false);
               redos.clear();
               update();
               repaint();
               if(resuelto)
                    JOptionPane.showMessageDialog(null, "Solucion Satisfactoria hallada por el algorimo Backtracking","Notificacion de Exito", JOptionPane.INFORMATION_MESSAGE);
              else JOptionPane.showMessageDialog(null, "El sudoku no se ha podido resolver, compruebe sus asignaciones ","Mensaje de Error", JOptionPane.ERROR_MESSAGE);


            }
        });
        //Vuelve atras en un movimiento
        undo= new JButton("Undo");
        undo.setFont(new Font("forte",9,16));
        undo.setBounds(60,550, 100, 50);
        undo.setEnabled(false);
        undo.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent action){
               redo.setEnabled(true);
               freeze.setEnabled(true);
               Model copia = undos.pop();
               if(!sudoku.isNullCandidates())
                    redos.add(new Model(sudoku));
               else
                    redo.setEnabled(false);
               sudoku = new Model(copia);
               if(undos.size()==0)
                  undo.setEnabled(false);
               update();
               repaint();
            }
        });
        //Rehace un movimiento
        redo= new JButton("Redo");
        redo.setFont(new Font("forte",9,16));
        redo.setBounds(170,550, 100, 50);
        redo.setEnabled(false);
        redo.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent action){
               Model copia = redos.pop();
               undos.add(new Model(sudoku));
               sudoku= new Model(copia);
               freeze.setEnabled(true);
               undo.setEnabled(true);
               if(redos.size()==0)
                   redo.setEnabled(false);
               update();
               repaint();
            }
        });
        //Vacia las pilas de redo and undo
        freeze= new JButton("Freeze");
        freeze.setFont(new Font("forte",9,16));
        freeze.setBounds(280,550, 100, 50);
        freeze.setEnabled(false);
        freeze.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent action){
               freeze.setEnabled(false);
               undo.setEnabled(false);
               redo.setEnabled(false);
               undos.clear();
               redos.clear();
            }
        });
        this.add(solve);
        this.add(undo);
        this.add(redo);
        this.add(freeze);

    }

    /**Vuelve invisible todas las etiquetas pertenecientes a un determinado panel
     */
    public void doInvisibleCandidates(JPanel panel){
        for (int k = 0; k < 10; k++) {
             JLabel candLabel=(JLabel) panel.getComponent(k);
             candLabel.setVisible(false);
       }
    }

    /** Actualiza la interfase grafica, cada que ocurre un cambio en el Modelo
     */
    public void update(){

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                doInvisibleCandidates(board[i][j]);
                int value = sudoku.getCellValue(i, j);
                if(value!=0 && value!=-1 ){
                    board[i][j].remove(0);
                    JLabel label= new JLabel();
                    label.setFont(new Font("forte",9,40));
                    label.setForeground(Color.black);
                    label.setText(""+value);
                    label.setBounds(20, 10, 40,40);
                    label.setVisible(true);
                    board[i][j].add(label, 0);
                }
                if(value==0){
                    JLabel label=(JLabel) board[i][j].getComponent(0);
                    label.setVisible(false);
                    for(int cand:sudoku.getCell(i, j)){
                        JLabel candLabel=(JLabel) board[i][j].getComponent(cand);
                        candLabel.setText(""+cand);
                        candLabel.setVisible(true);
                    }
                }
                if(value==-1){
                    ImageIcon imagen= new ImageIcon("calavera.gif");
                    JLabel label=(JLabel) board[i][j].getComponent(0);
                    label.setIcon(imagen);
                    label.setBounds(0, 0, 60, 60);
                    label.setVisible(true);
                }
            }
        }
    }

    /**
     * Funcion Princial, carga un archivo con la informacion del modelo
     * y llama al constructor de la clase vista
     */
    public static void main(String args[]) throws FileNotFoundException, IOException {
        //File archivo = new File ("board_escargot.txt");
        //File archivo = new File ("board_1.txt");
        //File archivo = new File ("board_2.txt");
        File archivo = new File ("board.txt");
        Watch vista = new Watch(archivo);
        vista.setVisible(true);
    }

}
