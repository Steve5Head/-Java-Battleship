import java.util.Scanner;


/* BattleShips Game 
 * 
 * This program is a game in which the player is against either a player or a computer 
 * and each side have a 5 battleships to deploy on a 10*10 grid, the first one who 
 * sink every battleship wins.
 * 
 * +: hit
 * ╳: missed
 */


public class bs
{
	static Scanner input = new Scanner(System.in);
	
	final static int nbPlayer = 2, nbRows = 10, nbCols = 10;
	// even if the 2nd "player" is the computer the 2nd grid is created.
	static int nbBattleship1 = 5, nbBattleship2 = 5;
	static String pseudo1, pseudo2;
	static boolean computer;
	
	public static int[][][] gridPlayer = new int[nbPlayer][nbRows][nbCols]; // Grid where you have stock your battleship.	

	public static void main(String[] args) 
	{	
		int choice, player, enemy, playFirst;
		
		
		System.out.println("HELLO!");
		rules();
		
		do
		{
			System.out.print("\nMODE:\n"
					+ "\t1. PLAYER VS PLAYER\n"
					+ "\t2. PLAYER VS COMPUTER\n"
					+ "> ");
			choice = Integer.parseInt(input.nextLine());
			
			if(choice<1 || choice>2)
				System.out.println("ONLY THOSE 2 CHOICES ARE POSSIBLE! PLEASE RETRY!");
			
		}while(choice<1 || choice>2);	
		
		if(choice==2)
			computer=true;
		
		do
		{
			System.out.print("PLEASE PLAYER 1, ENTER YOUR PSEUDO: ");
			pseudo1 = input.nextLine();
			if(computer==false)
			{
				System.out.print("PLEASE PLAYER 2, ENTER YOUR PSEUDO: ");
				pseudo2 = input.nextLine();
			}
			
			if(pseudo1.equals(pseudo2))
				System.out.println("YOU CAN'T HAVE THE SAME PSEUDO!");
			
		}while(pseudo1.equals(pseudo2));
		
		createOcean();
		playFirst = headOrTail();
			
		initialization();
		
		switch(playFirst)
		{
			case 1://player1 first
				player=0;
				enemy=1;
				break;
			case 2://player2 first
			default://computer first
				player=1;
				enemy=0;
				break;
		}
		
		if(choice==1)
			battlePVP(player, enemy);
		else
			battlePVE(player, enemy);
		
		
		win(choice);
	}
	
//============================Functions==============================
	
	
//_________________________ Main Functions ___________________________
	
	public static void deployManually(int player) //this function specific to the player. It allow the player to manually deploy all the battleships in his grid
	{
		int positionShip;
		
		for(int nbBattleships = 5; nbBattleships>0; nbBattleships--)
		{	
			int shipLength, xh, yh, positionOK;
			//(xh,yh): coordinates of the head of the warship.
			
			System.out.println("Your actual grid:");
			displayOwnOcean(player);
			
			do //check if the coordinates are legit or not (a boolean could have been use in this situation if there weren't any error display.)
			{
				switch (nbBattleships) 
				{
					case 5:
						shipLength = 5;
						break;
					case 4:
						shipLength = 4;
						break;
					case 3:
					case 2:
						shipLength = 3;
						break;
					default:
						shipLength = 2;
						break;	
				}
				System.out.println("The length of the ship is " + shipLength + " squares");
				System.out.println("Choose the position of ship n°" + (6 - nbBattleships) + ":\n"
						+ "1.         2.   ^     3.         4.    +     \n"
						+ "                +                      +     \n"
						+ "  < + + +       +        + + + >       +     \n"
						+ "                +                      v     \n"
						+ "\n \"<\" represents the \"head\" of the  ship, the length isn't representative.");
	
				System.out.print("Enter the position you want: ");
				positionShip = Integer.parseInt(input.nextLine());
	
				while(positionShip<1 || positionShip>4)
				{
					System.out.print("ERROR: only 4 positions available.\n"
							+ "Enter the position you want: ");
					positionShip = Integer.parseInt(input.nextLine());
				}

			
				System.out.print("Enter X coordinate: ");
				xh = Integer.parseInt(input.nextLine()); 
				System.out.print("Enter Y coordinate: ");
				yh = Integer.parseInt(input.nextLine());
				
				positionOK = checkPositionning(player, xh, yh, positionShip, shipLength);
				
				if(positionOK!=0)
				{
					if(positionOK == 1)
						System.out.println("ERROR: a coordinate is incorrect, the ocean is a 10*10 grid. Please. Retry...\n");
					else if(positionOK == 2)
						System.out.println("ERROR: ship out of the grid. Please. Retry...\n");	
					else 
						System.out.println("ERROR: there is a obstable on the square indicated. Please. Retry...\n");	
				}
				
			}while(positionOK!=0);
			
			// complete the grid with "1" which represents a part of ship.
			for(int square = shipLength; square>0; square--) 
			{
				gridPlayer[player][xh][yh] = 6-nbBattleships;
				switch (positionShip) 
				{
					case 1:
						xh++;
						break;
					case 2:
						yh++;
						break;
					case 3:
						xh--;
						break;
					default:
						yh--;
						break;	
				}
			}
		}
		displayOwnOcean(player);
		
		if(player==0)
			System.out.println(pseudo1 + "'s ship have been deployed.");
		else
			System.out.println(pseudo2 + "'s ship have been deployed.");
		
		System.out.println("\n-------------------------------------------------------------------");
		
	}
	public static void deployRandomly(int player) //this function will randomly position every ships in the player's grid
	{
		int positionShip;
		
		for(int nbBattleships = 5; nbBattleships>0; nbBattleships--)
		{	
			int shipLength, xh, yh, positionOK;
			
			do //check if the coordinates are legit or not (a boolean could have been use in this situation if there weren't any error display.)
			{
				switch (nbBattleships) 
				{
					case 5:
						shipLength = 5;
						break;
					case 4:
						shipLength = 4;
						break;
					case 3:
					case 2:
						shipLength = 3;
						break;
					default:
						shipLength = 2;
						break;	
				}
				
				positionShip = (int)(Math.random()*4) + 1; //" + 1 " allow the position to be between 1 and 4
			
				//X coordinate of the head of the ship
				xh = (int)(Math.random()*10);
				//Y coordinate of the head of the ship
				yh = (int)(Math.random()*10);
				
				positionOK = checkPositionning(1, xh, yh, positionShip, shipLength);
				
			}while(positionOK!=0);
			
			for(int square = shipLength; square>0; square--) 
			{
				gridPlayer[player][xh][yh] = 6-nbBattleships;
				switch (positionShip) 
				{
					
					case 1:
						xh++;
						break;
					case 2:
						yh++;
						break;
					case 3:
						xh--;
						break;
					default:
						yh--;
						break;	
				}
			}
		}
		
		if(player==0)
			System.out.println(pseudo1 + "'s ship have been deployed.");
		else if(player==1 && computer==false)
			System.out.println(pseudo2 + "'s ship have been deployed.");
		else
			System.out.println("Computer's ship have been deployed.");
	}
	public static void initialization() //It is a menu that will allow the player to choose whether he wants to position manually or automatically (randomly)
	{
		int choice = 0;
		
		for(int player=0; player<2; player++)
		{
			if(player==0 || (player==1 && computer==false))
			{
				do
				{
					if(player==0)
						System.out.print(pseudo1);
					else if(player==1 && computer==false)
						System.out.print(pseudo2);
					System.out.print(", do you want to position the ship yourself ?"
							+ "\n  1.Yes"
							+ "\n  2.No"
							+ "\n> ");
					choice = Integer.parseInt(input.nextLine());
					
					if(choice!=1 && choice!=2)
						System.out.println("You only have 2 choices. Please retry!");
					
				}while(choice!=1 && choice!=2);
				
				if(choice==1)
					deployManually(player);
				else
					deployRandomly(player);
			}
			else 
				deployRandomly(player);
		}
	}
	
	public static void battlePVP(int player, int enemy) //function dedicate to the player-versus-player's game
	{
		do
		{
			
			displayOcean(enemy);
			playersTurn(player);
			
			if(player==0)
			{
				player++;
				enemy--;
			}
			else
			{
				player--;
				enemy++;
			}
			
			next();
			
		}while(nbBattleship1 != 0 && nbBattleship2 != 0);
	}
	public static void battlePVE(int player, int enemy) //function dedicate to the player-versus-environnement's game
	{
		do
		{			
			if(player==0)
			{
				displayOcean(enemy);
				playersTurn(player);
			}
			else 
				computersTurn();
			
			if(player==0)
			{
				player++;
				enemy--;
			}
			else
			{
				player--;
				enemy++;
			}
			
			next();
			
		}while(nbBattleship1 != 0 && nbBattleship2 != 0);
	}
	
	public static void playersTurn(int player) //this function gather everything that will happen during the player's turn
	{
		int xs, ys, shotOK, shipHit, enemy;
		boolean shipSunk;
		String pseudo;
		
		if(player==0)
		{
			pseudo = pseudo1;
			enemy = 1;
		}
		else
		{
			pseudo = pseudo2;
			enemy = 0;
		}
		
		System.out.println(pseudo + "'s turn");
		
		do
		{
			System.out.print("Enter X coordinate: ");
			xs = Integer.parseInt(input.nextLine());
			System.out.print("Enter Y coordinate: ");
			ys = Integer.parseInt(input.nextLine());
			
			shotOK = checkShot(enemy, xs, ys);
				
			if(shotOK==1)
				System.out.println("ERROR: a coordinate is incorrect, the ocean is a 10*10 grid. Please. Retry...\n");
			else if(shotOK==2)
				System.out.println("ERROR: you can't shoot where you already shot. Please. Retry...\n");
			
		} while(shotOK!=0);
		
		

		if(gridPlayer[enemy][xs][ys]==0)
		{
			gridPlayer[enemy][xs][ys] = -6;
			displayOcean(enemy);
			System.out.println("\t< Missed >");
		}
		else 
		{
			shipHit = gridPlayer[enemy][xs][ys];
			gridPlayer[enemy][xs][ys] = -shipHit;
			
			shipSunk = checkShipSunk(enemy, shipHit, xs, ys);
			displayOcean(enemy);

			if(shipSunk==true)
			{
				if(player==0)
					nbBattleship2--;
				else
					nbBattleship1--;
				
				System.out.println("\t>  Hit and Sunk! <\t");
			}
			else 
				System.out.println("\t> Hit! <\t");
		}

		
		
	}
	// this function gather everything that will happen during the computer's turn
	public static void computersTurn()//only random shots
	{
		int xs, ys, shotOK, shipHit;
		boolean shipSunk;
		
		System.out.println("Computer's turn");
		
		do
		{
			// X coordinate of the shot
			xs = (int)(Math.random()*10);
			// Y coordinate of the shot
			ys = (int)(Math.random()*10);
			
			shotOK = checkShot(0, xs, ys);//not necessary to be an int but it still check the shot
			
		}while(shotOK!=0);
			
		System.out.println("Computer shoot at (" + xs + ", " + ys + ")");
		
		if(gridPlayer[0][xs][ys]==0)
		{
			gridPlayer[0][xs][ys] = -6;
			displayOcean(0);
			
			System.out.println("\t< Missed >");
		}
		else
		{
			shipHit = gridPlayer[0][xs][ys];
			gridPlayer[0][xs][ys] = -shipHit;
			
			shipSunk = checkShipSunk(0, shipHit, xs, ys);
			displayOcean(0);
			
			if(shipSunk==true)
			{
				System.out.println("\t>  Hit and Sunk! <\t");
				nbBattleship1--;
			}
			else 
				System.out.println("\t> Hit! <\t");
		}
	}
	public static void computersTurnIA()//random shots until a part of a ship is hit
	{
		
	}
	
	
	
//_________________________ Sub Functions ___________________________

	public static void createOcean() //this function create grids for players (including the computer) 
	{
		for(int player=0; player<gridPlayer.length; player++)
		{
			for(int x=0; x<gridPlayer[player].length; x++)
			{
				for(int y=0; y<gridPlayer[player][x].length; y++)
				{
					gridPlayer[player][x][y] = 0;
				}
			}
		}
	}
	
	public static void displayOwnOcean(int player) //function which display the player's grid, where are his own battleships 
	{
		for(int y=0; y<gridPlayer[player].length; y++)
		{
			if(y==0)
			{
				System.out.print("\n   _" );
				
				for(int top=0; top<gridPlayer[player].length; top++)
				{
					System.out.print("__");
				}
				System.out.println();
			}
			
			System.out.print(y + "  ");
			for(int x=0; x<gridPlayer[player][y].length; x++)
			{
				System.out.print("|" + gridPlayer[player][x][y]);
			}
			System.out.println("|");
			
			if(y==gridPlayer[player].length-1)
			{
				System.out.print("   ‾" );
				for(int top=0; top<gridPlayer[player].length; top++)
				{
					System.out.print("‾‾");
				}
				System.out.println();
				
				System.out.print("    ");
				for(int absciss=0; absciss<gridPlayer[player].length; absciss++)
				{
					System.out.print(absciss + " ");
				}
				System.out.println();
			}
		}
	}
	public static void displayOcean(int player)
	{	
		for(int y=0; y<gridPlayer[player].length; y++)
		{
			if(y==0)
			{
				System.out.print("\n   _" );

				for(int top=0; top<gridPlayer[player].length; top++)
				{
					System.out.print("__");
				}
				System.out.println();
			}

			System.out.print(y + "  ");
			for(int x=0; x<gridPlayer[player][y].length; x++)
			{
				if(gridPlayer[player][x][y]==-6)
					System.out.print("|╳");
				else if(gridPlayer[player][x][y]<=-1 && gridPlayer[player][x][y]>=-5)
					System.out.print("|+");
				else
					System.out.print("|·");
			}
			
			System.out.println("|");

			if(y==gridPlayer[player].length-1)
			{
				System.out.print("   ‾" );
				for(int top=0; top<gridPlayer[player].length; top++)
				{
					System.out.print("‾‾");
				}
				System.out.println();

				System.out.print("    ");
				for(int absciss=0; absciss<gridPlayer[player].length; absciss++)
				{
					System.out.print(absciss + " ");
				}
				System.out.println();
			}
		}
	}
	//this function return an integer which indicates whether the positioning is correct or not (instead of a boolean to have a specific message) 
	
	public static int checkPositionning(int player, int x, int y, int positionShip, int shipLength) 
	{
		if(x<0 || x>9 || y<0 || y>9)
			return 1;
		if((positionShip == 1 && x+shipLength>=10) || (positionShip == 2 && y+shipLength>=10) || (positionShip == 3 && x-shipLength<-1) || (positionShip == 4 && y-shipLength<-1))
			return 2;
		else
			for(int square = shipLength; square>0; square--) 
			{
				if(gridPlayer[player][x][y] != 0)
					return 3;
				else
				{
					switch (positionShip) 
					{
						case 1:
							x++;
							break;
						case 2:
							y++;
							break;
						case 3:
							x--;
							break;
						default:
							y--;
							break;	
					}
				}
			}
		
		return 0;
	}
	public static int checkShot(int enemy, int xs, int ys) //this function return an integer which indicates whether the shot is correct or not (instead of a boolean to have a specific message) 
	{
		if(xs<0 || xs>9 || ys<0 || ys>9)
			return 1;
		else if(gridPlayer[enemy][xs][ys]<=-1 && gridPlayer[enemy][xs][ys]>=-6)
			return 2;
		
		return 0;
	}
	public static boolean checkShipSunk(int enemy, int shipHit, int xs, int ys) // this function check if there is any sunk ship 
	{
		boolean shipSunk = true;

		for(int x=0; x<gridPlayer[enemy].length; x++)
		{
			for(int y=0; y<gridPlayer[enemy][x].length; y++)
			{
				if(gridPlayer[enemy][x][y] == shipHit)// if there is still at least 1 part of the ship we are search for in the the grid.
					shipSunk = false;
			}
		}
		return shipSunk;
	}
	
	public static int headOrTail() //this function randomly chose the player that start first by returning an integer 
	{
		int coin = (int)(Math.random()*2);
		
		if(coin==0)
		{
			System.out.println(pseudo1 + " plays first.\n");
			return 1;
		}
		else if(coin==1 && computer==false)
		{
			System.out.println(pseudo2 + " plays first.\n");
			return 2;
		}
		else
		{
			System.out.println("Computer plays first.\n");
			return 0;
		}
	}
	
	public static void win(int choice) //function that display a message depending of the situation of the end of the battle
	{
		if(choice == 1 && nbBattleship1 == 0)
			System.out.println("\n\tBRAVO " + pseudo2 + ", YOU WON!\n\tMEHHH " + pseudo1 +", YOU LOST... NEXT TIME VICTORY WILL BE YOURS.");
		else if(choice == 1 && nbBattleship2 == 0)
			System.out.println("\n\tBRAVO " + pseudo1 + ",YOU WON!\n\tMEHHH " + pseudo2 +", YOU LOST... NEXT TIME VICTORY WILL BE YOURS.");
		else if(choice == 2 && nbBattleship2 == 0)
			System.out.println("\n\tBRAVO " + pseudo1 + ", YOU WON AGAINST A BOT! I GUESS THEY WON'T INVADE US SOON!");
		else
			System.out.println("\n\tyou lost against a bot...");
	}
	
	public static void rules()
	{
		String yesOrNo;

		System.out.println("DO YOU WANT TO READ THE RULES ? (y/n)");
		yesOrNo = input.nextLine();
		do
		{
			switch(yesOrNo)
			{
				case "y":
				case "Y":
					System.out.println("BattleShip consists to play against either a player or the computer (1v1). \nEach player have 5 ships:"
									+ "\n\t- Carrier (5 squares ship), "
									+ "\n\t- Battleship (4 squares ship), "
									+ "\n\t- Destroyer (3 squares ship), "
									+ "\n\t- Submarine (3 squares ship), "
									+ "\n\t- Patrol Boat (2 squares ship)."
									+ "\n\nThe first player who takes down every single ship of his enemy win. Pretty easy right!\n");
					break;
				case "n":
				case "N":
					System.out.println("Alright...");
					break;
				default:
					System.out.println("You can only say y or n...");
					break;
			}

			if(yesOrNo.equals("y"))
			{
				System.out.println("DO YOU WANT TO READ THE RULES AGAIN? (y/n)");
				yesOrNo = input.nextLine();
			}

		}while(yesOrNo.equals("y"));
	}
	
	public static void next()// function that is only useful to make a "break"
	{
		@SuppressWarnings("unused")
		String next;
		
		System.out.println("Press any button to continue.");
		next = input.nextLine();
		System.out.println("-------------------------------------------------------------------");
	}

}