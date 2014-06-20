import java.util.Random;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Main
{
	int [][] cards;
	int money = 2000;
	int hiLoCount;
	int pWins=0, dWins=0, draws=0;
	int bankAccount = 0;

	boolean debug = true;
	boolean debug2 = true;
	boolean manualPlayer = true;
	boolean withDrawFromBank = true;
	int bestMoney =0;
	int mostHands = 0;

	int startingMoney = 750;

	public static void main (String[] args)
	{
		System.out.println();
		Main main = new Main();
		main.cards = main.createDeck(1);
		boolean go = true;
		int matches = 1;
		Double best = 0.0;
		int bestBank=0;
		int totalHands=0;
		
		int amount = 200;

		int winnings = -2000;

		int bankAccountAvg=0;
		int avgHandsLasted=0;

		for(int i=0; i<matches; i++)
		{

			main.money =main.startingMoney;
			if(main.withDrawFromBank == false)main.bankAccount = 0;
			bestBank=0;
			totalHands = 0;

			while(main.money>0 || (main.bankAccount>=amount && main.withDrawFromBank == true))	//this will loop through possible thresholds
			{	
				//main.pWins=0;
				//main.dWins=0;
				//money extraction for this round of gambling
				if(main.money<=0 && main.bankAccount>=amount && main.withDrawFromBank == true)
				{
					main.bankAccount = main.bankAccount - amount;
					main.money = amount;
				}
				
				//main.startingMoney=main.bankAccount/4;

				//if(main.money<=0)break;

				int rounds = 0;
				int maxMoney = 0;
				Double avg = 0.0;

				//for(int i =0; i<matches; i++)
				//while(main.money>0)
				{
					if(main.bankAccount+main.money > bestBank) bestBank = main.bankAccount+main.money;

					if(main.money>maxMoney)maxMoney = main.money;
					int temp = main.startPlayerVsDealerGame(main.money/10);
					if(temp>0)
					{
						if(main.debug2)System.out.println("\t\tPlayer Wins      bestBank = " +bestBank);
						main.pWins++;
					}
					else if(temp<0)
					{
						if(main.debug2)System.out.println("\t\tDealer Wins");
						main.dWins++;
					}
					else main.draws++;
					//System.out.println("Player wins = " + temp + " \n");
					Double score = (main.pWins+0.0)/(main.pWins+0.0+main.dWins);
					avg += score;
					
					if(main.debug)System.out.println("Player wins: " + main.pWins + "  Dealer wins: " + main.dWins + "  Win Ratio: " + score);
					if(main.debug)System.out.println("Money: " + main.money);
					rounds++;

					if(main.money >= main.startingMoney + amount*10 )
					{
						
						main.money = main.money - amount;;
						main.bankAccount += amount;
						if(main.debug)System.out.println("Just deposited " + amount + " in bank account, which now has " + main.bankAccount/2 + " money stored in it!, now I have " + main.money + " money to gamble with!");
					}
				}



				//avg = avg/matches;
				totalHands += rounds;
				
			
				//System.out.println("\n HANDS LASTED: " + rounds + "      held a maxMoney at: " + maxMoney +", could have made " + (maxMoney - main.startingMoney) + " bucks but you have " + main.bankAccount + " saved up now!");
				
			}
			avgHandsLasted += totalHands;
			bankAccountAvg += bestBank;

			if(main.debug2)System.out.println("You're Bankrupt BRO! After " + totalHands + " hands too!\nAt one point you had " + bestBank + " money in bankAccount\n");

		}

		avgHandsLasted /= matches;
		bankAccountAvg /= matches;

		System.out.println("At the end of " + matches + " permutations, you had an average max net worth of " + bankAccountAvg + 
			" with starting money: " + main.startingMoney + " after an average of " + avgHandsLasted + " hands.");
		System.out.println("BankAccount: " + main.bankAccount + "   money: " + main.money);
		//System.out.println("Best threshold is " + bestNum + "% with win rate of: " + best );
		// 2 = 2, 3=3, ... , 10=10, 11=J, 12=Q, 13=K, 14=A
		/*

		
		for(int i =0; i<13; i++)
		{
			for(int j=0; j<4; j++)
			{
				main.cards[i][j] = i+2;
			}
		}
		while(main.deckCount()>0)
		{
			System.out.println("You drew a " + main.readCard(main.drawCard()));
			main.printDeck();
			System.out.println("count: " + main.deckCount());
		}
		*/
		//if(main.debug)System.out.println("goodbye");
	}

	public int startPlayerVsDealerGame(int baseBet)
	{
		//Bet more if count is high, bet less if count is low
		
		//1 if player wins, -1 if dealer wins, 0 if push
		int win = 0;
		//Walks through one complete blackjack game
		Scanner scan = new Scanner(System.in);
		boolean end = false;
		
		int dTotal =0, pTotal=0;
		ArrayList<Integer> dCards = new ArrayList<Integer>();
		ArrayList<Integer> pCards = new ArrayList<Integer>();

		//Deal out both dealer and player hand

		dCards.add(drawCard());
		pCards.add(drawCard());

		dCards.add(drawCard());
		pCards.add(drawCard());

		int count = hiLoCount();
		int mult = 1;
		for(int n=-20; n<20; n++)
		{
			if(count==n)
			{
				if(n>0)
				{
					baseBet = baseBet*n*mult;
				}
				else if(n<0)
				{
					baseBet = -baseBet/n*mult;
				}
				else
				{
					baseBet = baseBet*mult;
				}

				while(baseBet >= money/2)
				{
					baseBet--;
				}
				if(baseBet<=5 && money>=5)baseBet=5;
				else if(baseBet<=0)baseBet=1;
			}
		}
		if(debug2)System.out.println("\t\tPlaer: "+ pWins + "  Dealer: " + dWins + "\n\tMoney: "+money+"  BankAccount: " + bankAccount + "  hiLoCount: " + count + "  currentBet: " +baseBet );

		if(debug)System.out.println("\t\thiLoCount: " + count + " new baseBet is: " + baseBet);


		if(debug)System.out.println("\tdealer: " + getHand(dCards, true));
		if(debug)System.out.println("\tplayer: " + getHand(pCards, false));

		boolean skipMove=false;
		while(end == false)
		//for(int i=0; i<1000; i++);
		{

			//Player decides to hit/stay

			//Dealer goes to 17

			//Who wins

			printDeck();

			String move = "";
			if(skipMove == false)
			{
				//will only hit if chance to bust is 60% or less
				move= makeDecisionBrian(pCards, 0.0, false);
				//move = makeDecision(dCards, pCards);
				if(debug)System.out.println("Chance of dealer bust: " + makeDecisionBrian(dCards, 0.0, true));
				if(debug)System.out.println("Chance of player bust: " + makeDecisionBrian(pCards, 0.0, true));

				Double dPer = Double.parseDouble(makeDecisionBrian(dCards, 0.0, true));
				Double pPer = Double.parseDouble(makeDecisionBrian(pCards, 0.0, true));

				//45% < 90%   % is chance to bust
				if(makeDecision(dCards, pCards).equals("d")) move = "d";
				else if(pPer < 35 || makeDecision(dCards, pCards).equals("h")) move = "h";
				else move = "s";

				if(debug)System.out.println("reccomended move: " + move);
				if(debug)System.out.println("\tdealer: " + getHand(dCards, true));
				if(debug)System.out.println("\tplayer: " + getHand(pCards, false));
			}
			else skipMove = false;
			
			if(debug)System.out.print("enter h / s / d: ");
			String in="";
			
			if(manualPlayer == true)in = scan.next();
			else in = move;

			//in=move;

			//String in = move;



			if(in.toLowerCase().equals("h"))
			{
				int temp = drawCard();
				pCards.add(temp);
				if(debug)System.out.println("Player hits and gets: " + readCard(temp) + ", new total is " + calcHand(pCards));
				if(calcHand(pCards) > 21)
				{
					money = money - baseBet;
					if(debug)System.out.println("BUST!!");
					end = true;
					return -1;
				}
			}
			else if(in.toLowerCase().equals("d"))
			{
				int temp = drawCard();
				pCards.add(temp);
				baseBet *= 2;
				if(debug)System.out.println("Player doubles down (bet now: " +baseBet+") and gets: " + readCard(temp) + ", new total is " + calcHand(pCards));
				if(calcHand(pCards) > 21)
				{
					money = money - baseBet;
					if(debug)System.out.println("BUST!!");
					end = true;
					return -1;
				}
				end=true;
			}
			else if(in.toLowerCase().equals("s"))
			{
				end = true;
			}
			else if(in.toLowerCase().equals("p"))
			{
				skipMove = true;
				printDeck();
				if(debug)System.out.println("\tdealer: " + getHand(dCards, true));
				if(debug)System.out.println("\tplayer: " + getHand(pCards, false));
			}
			else
			{
				System.out.println("Not recognized");
			}

		}
		end = false;
		while(end == false)//now dealer hits to 17
		{
			
			if(calcHand(dCards) < 17)
			{
				int temp = drawCard();
				dCards.add(temp);
				if(debug)System.out.println("Dealer hits and gets: " + readCard(temp) + ", new total is " + calcHand(dCards));
			
			}
			else
			{
				if(debug)System.out.println("Dealer now has: " + calcHand(dCards));
				end = true;
			}
			
		}

		int pScore = calcHand(pCards);
		int dScore = calcHand(dCards);
		if(pScore > dScore || dScore>21)
		{
			
			//check for blackjack
			if((pCards.get(0)==14 || pCards.get(1)==14) && (cardValue(pCards.get(0))==10 || cardValue(pCards.get(1)) ==10))
			{
				if(debug)System.out.println("BLACKJACK! YOU WIN " + (baseBet*3)/2 + " money!");
				money += (baseBet*3)/2;
			}
			else
			{
				money += baseBet;
				if(debug)System.out.println("You win " + baseBet + " money!");
			}
			return 1;
		}
		else if(dScore > pScore)
		{
			money -= baseBet;
			if(debug)System.out.println("You Lose!");
			return -1;
		}
		else 
		{
			if(debug)System.out.println("Push");
			return 0;
		}
	}

	public int hiLoCount()
	{
		int count=0;
		for(int i=0; i<13; i++)
		{
			for(int j=0; j<4; j++)
			{
				if(cards[i][j] != -1)
				{
					if(cards[i][j] <=6) count--;
					else if(cards[i][j] >9) count++; 

				}
			}
		}
		return count;
	}

	public String makeDecisionBrian(ArrayList<Integer> hand, Double threshold, boolean returnAsDouble)
	{
		//find % to bust with current hand and current deck config
		//if % is low enough, hit
		//else stay

		//get all cards in deck, put in two piles
		//those that make me bust
		//and those that make me not bust
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<13; i++)
		{
			for(int j=0; j<4; j++)
			{
				if(cards[i][j] != -1)
				{
					//System.out.println("card val: " + cards[i][j]);
					list.add(cards[i][j]);
				}
			}
		}
		//now list holds all cards left in deck
		int willBust=0, wontBust=0;
		for(int i =0; i<list.size(); i++)
		{
			int newTotal = calcHand(hand) + cardValue(list.get(i));
			if(newTotal>21)
			{
				willBust++;
				//System.out.println("A '" + readCard(list.get(i)) + "' will give you a total of " + newTotal + " and you will BUST,  wontBust: " + wontBust + "  willBust: " + willBust);
				
			}
			else if(newTotal<21)
			{
				wontBust++;
				//System.out.println("A '" + readCard(list.get(i)) + "' will give you a total of " + newTotal + " and you will be safe,  wontBust: " + wontBust + "  willBust: " + willBust);
				
			}
		}
		Double chance = 100*(willBust+0.0)/(wontBust + willBust);
		//System.out.println("Percent of cards that will make you bust: " + chance + "%");
		if(returnAsDouble==true)return ""+chance;
		if(chance <= threshold) return "h";
		else return "s";
	}

	public String makeDecision(ArrayList<Integer> dHand, ArrayList<Integer> hand)
	{
		//returns 'h' for hit and 's' for stay
		int face = dHand.get(1);
		//lets have this one work by the books, no splits or anything
		int val = calcHand(hand);
		if(face == 2)
		{
			if(val<=8) return "h";
			else if(val==9) return "h";
			else if(val==10) return "d";
			else if(val==11) return "d";
			else if(val==12) return "h";
			else if(val==13) return "s";
			else if(val==14) return "s";
			else if(val==15) return "s";
			else if(val==16) return "s";
		}
		else if(face == 3)
		{
			if(val<=8) return "h";
			else if(val==9) return "d";
			else if(val==10) return "d";
			else if(val==11) return "d";
			else if(val==12) return "h";
			else if(val==13) return "s";
			else if(val==14) return "s";
			else if(val==15) return "s";
			else if(val==16) return "s";
		}
		else if(face == 4)
		{
			if(val<=8) return "h";
			else if(val==9) return "d";
			else if(val==10) return "d";
			else if(val==11) return "d";
			else if(val==12) return "s";
			else if(val==13) return "s";
			else if(val==14) return "s";
			else if(val==15) return "s";
			else if(val==16) return "s";
		}
		else if(face == 4 || face == 5 || face ==6)
		{
			if(val<=8) return "h";
			else if(val==9) return "d";
			else if(val==10) return "d";
			else if(val==11) return "d";
			else if(val==12) return "s";
			else if(val==13) return "s";
			else if(val==14) return "s";
			else if(val==15) return "s";
			else if(val==16) return "s";
		}
		else if(face == 7 || face == 8 || face == 9)
		{
			if(val<=8) return "h";
			else if(val==9) return "h";
			else if(val==10) return "d";
			else if(val==11) return "d";
			else if(val==12) return "s";
			else if(val==13) return "s";
			else if(val==14) return "s";
			else if(val==15) return "s";
			else if(val==16) return "s";
		}
		else 
		{
			if(val<=8) return "h";
			else if(val==9) return "h";
			else if(val==10) return "h";
			else if(val==11) return "d";
			else if(val==12) return "h";
			else if(val==13) return "h";
			else if(val==14) return "h";
			else if(val==15) return "h";
			else if(val==16) return "h";
		}

		return "s";

	}

	public String getHand(ArrayList<Integer> hand, boolean dealer)
	{
		String result="";
		int i =0;
		if(dealer == true)i=1;
		for(i=i; i<hand.size(); i++)
		{
			result += " " + readCard(hand.get(i));
		}
		if(dealer == false)return result + "\ttotal: " + calcHand(hand);
		else return result;
	}

	public int cardValue(int num)
	{
		if(num<=10) return num;
		else if(num<=13) return 10;
		else if(num==14) return 1;
		return -1;
	}

	public int calcHand(ArrayList<Integer> hand)
	{
		int total = 0;
		for(int i=0; i<hand.size(); i++)
		{
			int num = hand.get(i);
			int val = 0;
			if(num<=10) total+=num;
			else if(num==11) total+=10;
			else if(num==12) total+=10;
			else if(num==13) total+=10;
			else if(num==14)
			{
				if(total + 11 > 21) total+=1;
				else total+=11;
			}
		}
		return total;
	}

	public int[][] createDeck(int size)
	{	
		if(debug2)System.out.println("New Deck Created");
		int[][] temp = new int[13*size][4*size];
		for(int i =0; i<13*size; i++)
		{
			for(int j=0; j<4*size; j++)
			{
				temp[i][j] = i+2;
			}
		}
		return temp;
	}

	public void printDeck()
	{
		for(int i =0; i<4; i++)
		{
			for(int j=0; j<13; j++)
			{
				if(debug)System.out.print("[" + readCard(cards[j][i]) + "] ");
			}
			if(debug)System.out.println("");
		}

	}

	public int drawCard()
	{
		if(deckCount() ==0)
		{
			cards = createDeck(1);
		}
		boolean done = false;
		int result = -1;
		Random generator = new Random();
		int rand3 = generator.nextInt(deckCount());
		int walk = 0;
		for(int i=0; i<13; i++)
		{
			for(int j=0; j<4; j++)
			{
				if(cards[i][j] != -1)
				{
					if(walk==rand3)
					{

						result = cards[i][j];
						done = true;
						cards[i][j] = -1;
						return result;
					}
					else walk++;
				}
			}
		}
		
		return -1;
	}

	public int deckCount()
	{
		int count = 0;
		for(int i =0; i<13; i++)
		{
			for(int j=0; j<4; j++)
			{
				if(cards[i][j] != -1)count++;
			}
		}
		return count;
	}

	public String readCard(int num)
	{
		if(num<2) return " ";
		else if(num<=10)return num+"";
		else if(num==11)return "J";
		else if(num==12)return "Q";
		else if(num==13)return "K";
		else if(num==14)return "A";

		return "";
	}

}
