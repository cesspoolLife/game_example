package com.cesspoollife.sunday;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * 게임 로직 객체
 * 싱글톤 객체
 */
public class Shisensho {
	private static final Shisensho instance = new Shisensho();
	private int[][] cards;
	private int[] diff = {-12, 1, 12,-1};
	private List<Integer> path;
	private boolean isFinish;
	
	private Shisensho(){
	}
	
	public static Shisensho getInstace(){
		return instance;
	}
	
	/*
	 * 패를 배치하고 생성하는 메소드
	 * value가 0일 경우 패가 없는 곳
	 * 10*11이자만 외벽을 한번 감싸주기 때문에 12*13의 배열이 생성
	 */
	public void makeCard(int stage){
		this.cards = new int[12][13];
		List<Integer> matrix = new ArrayList<Integer>();
		
		//먼저 0으로 초기화
		for(int i=0;i<12;i++)
			for(int j=0;j<13;j++)
				this.cards[i][j] = 0;
		
		//stage 마다 패의 개수를 설정
		int size = 22*stage;
		
		//값을 넣기 전에 먼저 패를 넣을 위치를 만든다.
		int i =0;
		while(i!=size){
			int x = (new Random()).nextInt(10)+1;
			int y = (new Random()).nextInt(11)+1;
			int pos = matrix.indexOf(x+y*12);//임의로 받은 위치가 이미 지정되었는지 여부를 판단해서 비어있는 곳만 추가
			if(pos==-1){
				matrix.add(x+y*12);
				i++;
			}
		}
		
		//지정한 위치에 1~10까지의 임의의 수를 저장, 항상 짝수 개를 맞추기 위해 같은 값을 두곳에 저장
		size = matrix.size()/2;
		for(i=0;i<size;i++){
			int value = (new Random()).nextInt(10)+1;
			int first = matrix.remove(0);
			this.cards[first%12][first/12] = value;
			int second = matrix.remove(0);
			this.cards[second%12][second/12] = value;
		}
	}
	
	/*
	 * 배열을 String으로 변환
	 */
	public String toString(){
		String tmp= "";
		for(int j=0;j<13;j++){
			for(int i=0;i<12;i++){
				tmp += String.valueOf(cards[i][j]);
			}
			tmp += "\n";
		}
		return tmp;
	}
	
	/*
	 * 두 곳의 위치를 받아서 두 곳의 path를 리턴.
	 * path의 size가 0이면 성립되지 않음.
	 */
	public List<Integer> isMatch(int first, int second){
		path = new ArrayList<Integer>();
		//두 곳의 값이 같을 때만 탐색한다.
		if(get(first)==get(second))
			search(-1, first, second, 0);
		return path;
	}
	
	private boolean search(int direction, int position, int target, int turning )
	{
		if(turning>3)
			return false;
		if(position==target){
			path.add(position);
			return true;
		}
		if(get(position)!=0&&direction!=-1)
			return false;
		List<Integer> order = getOrder(position, target);
		int t;
		boolean isHit=false;
		for(int i : order){
			if((direction+2)%4==i&&direction!=-1)
				continue;
			if(direction==i)
				t = turning;
			else
				t = turning+1;
			isHit = search(i, position+diff[i], target, t);
			if(isHit){
				path.add(position);
				break;
			}
		}
		
		return isHit;
	}
	
	/*
	 * 0 : 위쪽
	 * 1 : 오른쪽
	 * 2 : 아래쪽
	 * 3 : 왼쪽 
	 */
	private List<Integer> getOrder(int position, int target){
		List<Integer> order = new ArrayList<Integer>();

		int px = getX(position);
		int py = getY(position);
		int tx = getX(target);
		int ty = getY(target);
		
		if(tx==px){
			if(ty>py){
				order.add(2);
				order.add(1);
				order.add(3);
			}
			else{
				order.add(0);
				order.add(3);
				order.add(1);
			}
		}else{
			if(ty==py){
				if(tx>px){
					order.add(1);
					order.add(0);
					order.add(2);
				}
				else{
					order.add(3);
					order.add(2);
					order.add(0);
				}
			}else{
				if(tx>px)
					order.add(1);
				else if(tx<px)
					order.add(3);
				if(ty>py)
					order.add(2);
				else if(ty<py)
					order.add(0);
				if(tx>px&&px!=0)
					order.add(3);
				else if(tx<px&&px!=11)
					order.add(1);
				if(ty>py&&py!=0)
					order.add(0);
				else if(ty<py&&py!=12)
					order.add(2);
			}
		}
		
		return order;
	}
	
	public boolean isAvailable(){
		path = new ArrayList<Integer>();
		boolean avail= false;
		isFinish = true;
		int i,j=0;
		for(i=0;i<156;i++){
			if(i%12==0||i%12==11||i/12==0||i/12==12||get(i)==0)
				continue;
			isFinish = false;
			for(j=0;j<156;j++){
				if(j%12==0||j%12==11||j/12==0||j/12==12||get(j)==0||i==j)
					continue;
				if(get(i)==get(j))
					search(-1, j, i, 0);
				if(!path.isEmpty()){
					avail = true;
					break;
				}
			}
			if(avail)
				break;
		}
		return avail;
	}
	
	/*
	 * isAvailable메소드를 통해서 생성된 isFinish의 값을 리턴
	 */
	public boolean isFinish(){
		return isFinish;
	}
	
	/*
	 * 패의 값을 0으로
	 */
	public void removeCard(int position){
		cards[position%12][position/12] = 0;
	}
	
	/*
	 * 전체 패의 크기(외벽포함)
	 */
	public int size(){
		return 156;
	}
	
	/*
	 * position으로 값을 리턴
	 */
	public int get(int position){
		return cards[position%12][position/12];
	}
	
	/*
	 * x값과 y값으로 값을 리턴
	 */
	public int get(int x, int y){
		return cards[x][y];
	}
	
	/*
	 * position의 X
	 */
	public int getX(int position){
		return position%12;
	}
	
	/*
	 * position의 Y
	 */
	public int getY(int position){
		return position/12;
	}
}
