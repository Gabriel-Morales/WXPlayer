package application.model;

public class AspectRatio {
	
	private int numerator;
	private int denominator;
	
	public AspectRatio(int num1, int num2)
	{
	
		int GCD = findGCD(num1, num2);
		numerator = num1 / GCD;
		denominator = num2 / GCD;
		
	}
	
	public int getDenominator()
	{
		return denominator;
	}
	
	public int getNumerator()
	{
		return numerator;
	}
	
	private int findGCD(int num1, int num2)
	{
		int larger , less;
		if (num1 > num2)
		{
			larger = num1;
			less = num2;
		}
		else
		{
			larger = num2;
			less = num1;
		}
		
		if (num1 == num2)
		{
			return 1;
		}
		

		if (less == 0)
		{
			return larger;
		}
		
		int result = larger % less;
		
		while (less != 0)
		{
			
			larger = less;
			less = larger % less;
			
		}
		
		return result;
	}
	

}
