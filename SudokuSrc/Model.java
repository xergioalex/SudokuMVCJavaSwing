import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

// @autor XergioAleX

/**
 *  La clase Model es una estructura, la cual posee todos los atributos y comportamientos para la construccion
 *  de Sudokus, dichos objetos consisten en un tablero 9x9 conformado por 9 regiones continuas de igual tamano cada una,
 *  las cuales se llenan con numeros del 1 al 9, cumpliendo los requisitos de no poder repetirsen numeros
 *  en la misma fila, columna o region a la cual pertenesca cada casilla del Tablero 9x9
 */
public class Model  {
    /** Matriz de celdas que representa las casillas del sudoku, contiene candidatos o valores*/
    private Cell[][] cells = new Cell[9][9];
    /** Arreglo de cadenas que poseen la forma del las 9 regiones del sudoku*/
    private String[] shape= new String[9];

    /**
     * Construye una nueva estructura de Model partir de un Objeto ya existente, permitiendo
     * clonar con facilidad cualquier sudoku
     */
    public Model(Model a) {
        construirTablero();
        for (int i = 0; i < 9; i++)
            this.shape[i]=a.getShape()[i];
        for (int i = 0; i < 9; i++) {
             for (int j = 0; j < 9; j++)
                  cells[i][j].addObserver();
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (a.getCellValue(i, j) > 0)
                    this.cells[i][j].setValue(a.getCellValue(i, j));
            }
        }
    }

    /** Construye una nueva Estructura Model basado en la informacion suministrada por un
     *  archivo de texto, la cual establece la forma del sudoku y unos valores de inicializacion
     */
    public Model(File archivo) throws FileNotFoundException, IOException {
        construirTablero();
        FileReader fr = new FileReader (archivo);
        BufferedReader br = new BufferedReader(fr);
        StringTokenizer token;
        String linea;
        int cont=0;
        int pos;
        int value;
        int iter=0;
        while((linea=br.readLine())!=null){
            if(cont==9){
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++)
                        cells[i][j].addObserver();
                }
                cont++;
                continue;
            }
            if(cont<9){
                this.shape[cont]=linea;
                cont++;
            }
            else{
                token=new StringTokenizer(linea);
                pos= Integer.parseInt(token.nextToken());
                value=Integer.parseInt(token.nextToken());;
                this.cells[pos/9][pos%9].setValue(value);
                test(pos/9,pos%9);
            }
        }
        if(cont==9){
           for (int i = 0; i < 9; i++) {
               for (int j = 0; j < 9; j++)
                  cells[i][j].addObserver();
            }
        }
        fr.close();
    }

    //GETTERS

    /** Metodo que retorna la variable privada Shape, la cual posee la forma del sudoku
     */
    public String[] getShape() {
        return shape;
    }

    /** Metodo que obtiene el valor que contiene una celda, si aun hay varios
     *  candidatos en la celda devuelve 0 si la celda esta vacia devuelve -1
     */
    public int getCellValue(int i, int j) {
        return cells[i][j].getValue();
    }

    /** Metodo que retorna una lista con los posibles valores de una celda del Sudoku
     *  en una posicion (i,j)
     */
    public List<Integer> getCellValues(int i, int j) {
        return cells[i][j].getValues();
    }

    /** Metodo que retorna un objeto celda en una posicion (i,j), pertenenciente
     *  al atributo privado cells, tablero 9x9 que conforma el sudoku
     */
    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    //SETTERS


    /** Le define un valor al contenido de una celda
     */
    public void setCellValue(int i, int j, int value) {
        this.cells[i][j].setValue(value);

    }

    //METODOS DEL MODELO *

    /** Define cada una de las celdas del tablero con los candidatos del 1 al 9
     */
     public void construirTablero() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++)
                cells[i][j] = new Cell(i, j);
        }
     }

     /** Representacion en modo texto del tablero
      */
     @Override
     public String toString() {
        String string = "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                string += String.format("%s ", Integer.toString(getCellValue(i, j)));
                if (j == 2 || j == 5)
                    string += "| ";
            }
            string += "\n";
            if (i == 2 || i == 5)
                string += "---------------------\n";
        }
        return string;
     }

     /**Me muestra las celdas que estan observando a determinada celda en una
      * posicion (i,j), con sus respectivos candidatos
      */
     public void test(int row, int col) {
            int cellPos=row*9+col;
            System.out.println("Set "+cellPos+" "+cells[row][col].getValue());
            int mycell = Integer.parseInt(String.format("%c", shape[row].charAt(col)));
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    boolean isSame = (i == row) && (j == col);
                    boolean isSameLine = (i == row) || (j == col);
                    boolean isSecondary=false;
                    int mycontext = Integer.parseInt(String.format("%c", shape[i].charAt(j)));
                    if(mycell == mycontext)
                        isSecondary=true;
                    if (!isSame && (isSameLine || isSecondary)) {
                        int contextPos=i*9+j;
                        System.out.println("Posibles: "+ contextPos+" => "+cells[i][j]);
                    }
                }
            }
     }

     /** Me clona un Objeto Model(Sudoku)
      */
     public void clone(Model board){
         for (int i = 0; i < 9; i++) {
             for (int j = 0; j < 9; j++) {
                 this.cells[i][j].clone(board.getCell(i, j));
             }
         }
     }

    /** Retorna Verdadero si el sudoku esta resuelto
     */
    public boolean isSolve() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (getCellValue(i, j) == 0 || getCellValue(i, j) == -1)
                    return false;
            }
        }
        return true;
    }


    /**Algoritmo vuelta atras!!!, por medio de la fuerza bruta busca una solucion al sudoku
     */
    public  boolean backtracking(){
        for(int i=0; i<9;i++){
            for(int j=0; j<9;j++){
                if(this.isNullCandidates()) return false;
                if(!this.getCell(i, j).isSolve()){
                     for(int x:this.getCell(i, j)){
                         Model copia= new Model(this);
                         copia.setCellValue(i, j, x);
                         if(copia.backtracking()){
                             this.clone(new Model(copia));
                             return true;
                         }
                     }
                     return false;
                 }
            }
         }
         return true;
    }

    /**Funcion auxiliar del backtracking que busca en todo el Sudoku si
     * alguna celda se quedo nula
     */
    public boolean isNullCandidates(){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(cells[i][j].getValue()==-1)
                    return true;
            }
        }return false;
    }


    //*************************************************************************

    /**
     * Clase anidada Celda para el manejo de cada una de las casillas de un sudoku
     */
    public class Cell extends Observable implements Observer, Iterable<Integer> {
        /**Lista de Candidatos */
        private List<Integer> values = new ArrayList<Integer>();
        /**Atributo que representa si la celda esta resuelta*/
        private boolean isSolved = false;
        /** Fila en la cual se encuenta la celda*/
        private int row;
        /** Columna en la cual se encuentra la celda*/
        private int col;

        /**Construye una celda, llenando la lista de posibles valores del 1 al 9.
         */
        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            for (int n = 1; n <= 9; n++)
                values.add(n);
        }

        /** Agrega observadores a una celda, las cuales se encuentran en el mismo
         *  contexto de esta (fila, columna, region)
         */
        public synchronized void addObserver() {
            int mycell = Integer.parseInt(""+shape[row].charAt(col));
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    boolean isSame = (i == row) && (j == col);
                    boolean isSameLine = (i == row) || (j == col);
                    boolean isSecondary=false;
                    int mycontext = Integer.parseInt(""+shape[i].charAt(j));
                    if(mycell == mycontext)
                        isSecondary=true;
                    if (!isSame && (isSameLine || isSecondary)) {
                        super.addObserver(cells[i][j]);
                    }
                }
            }
        }

        /** Define un  valor conocido a una celda despues de limpiar y notificar
         */
        public void setValue(int value) {
            values.clear();
            values.add(value);
            isSolved = true;
            super.setChanged();
            super.notifyObservers(value);
        }

        /** Actualiza una celda definiendo su valor, y notificando a sus observadores
         */
        @Override
        public void update(Observable o, Object arg) {
            values.remove(arg);
            if (!isSolved && values.size() == 1) {
                isSolved=true;
                int value = getValues().get(0);
                setValue(value);
            }
        }

        /** una celda es solved si solo tiene un valor o candidato,
         *  el metodo retorna este valor, si aun existen diversos posibles candidatos
         *  retorna 0, y si no hay candidatos posibles retorna -1
         */
        public int getValue() {
            return (getValues().size() == 1) ? getValues().get(0) : (getValues().size() == 0) ? -1 : 0;
        }

        /**Retorna verdadero si la celda esta resuelta
         */
        public boolean isSolve() {
            return this.isSolved;
        }

        /** Retorna la lista de posibles candidatos de la celda
         */
        public List<Integer> getValues() {
            return values;
        }

        /**Representacion en modo texto de la lista de candidatos de la celda
         */
        @Override
        public String toString(){
            String cadena="";
            for (int i = 0; i < values.size(); i++)
                cadena += String.format("%s ", Integer.toString( values.get(i)));
            return cadena;
        }

        /**Retorna un iterador de los elementos de la lista values de posibles
         * candidatos de la celda
         */
        public Iterator<Integer> iterator() {
            return values.iterator();
        }

        /**Clona una celda de un un sudoku 9x9
         */
        public void clone(Cell celda){
            List<Integer> copia= new ArrayList<Integer>();
            copia.addAll(celda.getValues());
            values.clear();
            values.addAll(copia);
        }

    }


}
