package pt.ipbeja.estig.fifteen.model;

/**
 * Data for each piece movement Stores initial and final position
 * 
 * @author João Paulo Barros
 * @version 2014/05/18 - 2016/04/03
 */
public class Move
{
	private final Position begin;
	private final Position end;

	public Move(Position begin, Position end)
	{
		this.begin = begin;
		this.end = end;
	}

	/**
	 * Created move only if begin and end are inside the board
	 * 
	 * @param begin
	 * @param end
	 * @return new Move object if begin and end are inside, null otherwise
	 */
	public static Move createMove(Position begin, Position end)
	{
		return begin.isInside() && end.isInside() ? new Move(begin, end) : null;
	}

	/**
	 * @return the begin position
	 */
	public Position getBegin()
	{
		return this.begin;
	}

	/**
	 * @return the end position
	 */
	public Position getEnd()
	{
		return this.end;
	}

	/**
	 * Get the reversed move
	 * 
	 * @return reverse move
	 */
	public Move getReversed()
	{
		return new Move(this.end, this.begin);
	}

	@Override
	public String toString()
	{
		return "Move [begin=" + begin + ", end=" + end + "]";
	}

	/*
	 * automatically generated in eclipse, with Source->Generate hashCode() and
	 * equals() (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begin == null) ? 0 : begin.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		return result;
	}

	/*
	 * automatically generated in eclipse, with Source->Generate hashCode() and
	 * equals() (non-Javadoc)
	 * 
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
		Move other = (Move) obj;
		if (begin == null)
		{
			if (other.begin != null)
				return false;
		}
		else if (!begin.equals(other.begin))
			return false;
		if (end == null)
		{
			if (other.end != null)
				return false;
		}
		else if (!end.equals(other.end))
			return false;
		return true;
	}
}
