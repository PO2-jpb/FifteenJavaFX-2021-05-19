package pt.ipbeja.estig.fifteen.model;

/**
 * Positions from 0 to number of lines and columns minus 1
 * @author João Paulo Barros
 * @version 2014/05/18 - 2016/04/03
 */ 
public class Position
{
   private final int line, col;
   
   public Position(int line, int col)
   {
      this.line = line;
      this.col = col;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "(" + line + ", " + col + ")";
   }

   /**
    * @return the line
    */
   public int getLine()
   {
      return this.line;
   }

   /**
    * @return the col
    */
   public int getCol()
   {
      return this.col;
   }

   /**
    * Checks if position is inside the board
    * @return true if inside, false otherwise
    */
   public boolean isInside()
   {
      return Position.isInside(this.getLine(), this.getCol());
   }
   
   /**
    * Checks if line col are inside tha board
    * @param line
    * @param col
    * @return true if inside, false otherwise
    */
   public static boolean isInside(int line, int col)
   {
      return 0 <= line && line < FifteenModel.N_LINES && 
             0 <= col && col < FifteenModel.N_COLS;
   }

   /* automatically generated in eclipse, 
    * with Source->Generate hashCode() and equals()
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + col;
      result = prime * result + line;
      return result;
   }

   /* automatically generated in eclipse, 
    * with Source->Generate hashCode() and equals() 
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Position other = (Position) obj;
      if (col != other.col)
         return false;
      if (line != other.line)
         return false;
      return true;
   }
}
