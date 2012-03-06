
/**
 * Is thrown when a board is invalid or unsolvable or has no children.
 *
 */
public class NullBoardException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullBoardException(String eMessage){
		super(eMessage);
	}

}
