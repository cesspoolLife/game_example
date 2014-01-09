package com.oopsbaby.sunday;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * 게임 로직 객체
 * 싱글톤 객체
 */
public class Shisensho {
	
	private static final Shisensho instance = new Shisensho();
	private int[][] block;
	private final int[] diff = {-12, 1, 12,-1};
	private List<Integer> path;
	private boolean isFinish;
	
	private Shisensho(){
	}
	
	/*
	 * 싱클톤으로 디자인된 객체를 리턴해줌.
	 */
	public static Shisensho getInstace(){
		return instance;
	}
	
	/*
	 * 블럭 배치하고 생성하는 메소드
	 * value가 0일 경우 블럭가 없는 곳
	 * 10*11이자만 외벽을 한번 감싸주기 때문에 12*13의 배열이 생성
	 */
	public void makeBlock(int stage){
		this.block = new int[12][13];
		List<Integer> matrix = new ArrayList<Integer>();
		
		//먼저 0으로 초기화
		for(int i=0;i<12;i++)
			for(int j=0;j<13;j++)
				this.block[i][j] = 0;
		
		//stage 마다 블럭의 개수를 설정
		int size = 22*stage;
		
		//값을 넣기 전에 먼저 블럭을 넣을 위치를 만든다.
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
			this.block[first%12][first/12] = value;
			int second = matrix.remove(0);
			this.block[second%12][second/12] = value;
		} 
		
		//초기 배열이 연결 가능한지 확인해서 불가능하다면 다시 배열
		while(!isAvailable()){
			setReArrage();
		}
	}
	
	/*
	 * 배열을 재배치 하는 함수
	 * 값은 그대로고 위치만 서로 변경된다.
	 */
	public void setReArrage(){
		for(int i=1;i<11;i++){
			for(int j=1;j<12;j++){
				if(this.block[i][j]==0)
					continue;
				int x = (new Random()).nextInt(10)+1;
				int y = (new Random()).nextInt(11)+1;
				int temp = this.block[i][j];
				this.block[i][j] = this.block[x][y];
				this.block[x][y] = temp;
			}
		}
	}
	
	/*
	 * 배열을 String으로 변환
	 */
	public String toString(){
		String tmp= "";
		for(int j=0;j<13;j++){
			for(int i=0;i<12;i++){
				tmp += String.valueOf(block[i][j]);
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
			search(-1, first, second, 0);//두 위치로 탐색을 시작한다.
		return path;
	}
	
	/*
	 * position과 target으로 검색
	 */
	private boolean search(int direction, int position, int target, int turning )
	{
		if(turning>3)//첫 위치에서 진행할때 turning에 1을 추가하기때문에 3 초과면 실패
			return false;
		if(position==target){//position이랑 target이랑 같다면 연결 성공을 나타냄
			path.add(position);//path에 저장
			return true;
		}
		if(get(position)!=0&&direction!=-1)//위치에 장애물이 있다면 실패, 단 처음은 제외
			return false;
		List<Integer> order = getOrder(position, target);//상하좌우 순서를 저장.
		int t;
		boolean isHit=false;
		for(int i : order){// 순서대로 반복
			if((direction+2)%4==i&&direction!=-1)//진행방향의 역방향은 탐색할 이유가 전혀 없음
				continue;
			if(direction==i)//동일 방향으로 진행될때는 turning값 그대로
				t = turning;
			else//방향이 다르다면 turn할 것이기 때문에 turning값을 증가
				t = turning+1;
			//포지션을 해당 위치 이동시켜 재귀로 탐색(y축으로 이동할 때는 +-12씩 이동함 diff 변수 초기에 설정)
			isHit = search(i, position+diff[i], target, t);
			if(isHit){//재귀가 true를 반환한다면 연결 가능한 거기 때문에
				path.add(position);//패스를 저장한다. 저장하는 패스는 최단거리다.
				break;//검색할 이유가 없으니 반복문을 빠져나온다.
			}
		}
		
		return isHit;
	}
	
	/*
	 * 탐색 순서를 정함.
	 * target에 가까워 지려는 방향을 먼저 저장해서 찾아갈수 있게 한다.
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
		
		if(tx==px){ //X축 기준 같은 라인에 있는 경우는 무조건 반대 방향을 검색할 필요가 없으므로 제외 
			if(ty>py){
				order.add(2);//바라보는 방향 우선 탐색
				order.add(1);
				order.add(3);
			}
			else{
				order.add(0);//바라보는 방향 우선 탐색
				order.add(3);
				order.add(1);
			}
		}else{
			if(ty==py){ //Y축 기준 같은 라인에 있는 경우는 무조건 반대 방향을 검색할 필요가 없으므로 제외
				if(tx>px){
					order.add(1);//바라보는 방향 우선 탐색
					order.add(0);
					order.add(2);
				}
				else{
					order.add(3);//바라보는 방향 우선 탐색
					order.add(2);
					order.add(0);
				}
			}else{//X,Y 모두 다른 경우로 상하 좌우 모두 저장 항상 가까워 질수 있는 방향을 우선 저장한다..
				if(tx>px)
					order.add(1);
				else if(tx<px)
					order.add(3);
				if(ty>py)
					order.add(2);
				else if(ty<py)
					order.add(0);
				if(tx>px&&px!=0)//범위를 초과 하지 않도록 하기 위해 조건을 추가.
					order.add(3);
				else if(tx<px&&px!=11)//범위를 초과 하지 않도록 하기 위해 조건을 추가.
					order.add(1);
				if(ty>py&&py!=0)//범위를 초과 하지 않도록 하기 위해 조건을 추가.
					order.add(0);
				else if(ty<py&&py!=12)//범위를 초과 하지 않도록 하기 위해 조건을 추가.
					order.add(2);
			}
		}
		
		return order;
	}
	
	/*
	 * 게임이 진행 가능한지 여부를 확인 하는 함수.
	 * 순차적으로 탐색을 하면서 처음으로 확인되면 반복문을 빠져나온다.
	 * 이때 스테이지 클리어 여부도 확인.(isFinish 변수)
	 */
	public boolean isAvailable(){
		path = new ArrayList<Integer>();
		boolean avail= false;
		isFinish = true; //끝 여부를 판단하는 변수
		int i,j=0;
		for(i=0;i<156;i++){
			if(get(i)==0)//block이 있는 곳만 검사
				continue;
			isFinish = false;//block이 있다는 의미임
			for(j=0;j<156;j++){
				if(get(j)==0||i==j)//block이 있는 곳만 검사, 기준점과 같은 곳은 검사하면 안됨.
					continue;
				if(get(i)==get(j))
					search(-1, j, i, 0);//두 지점을 가지고 search
				if(!path.isEmpty()){//Path가 있다면 연결 가능한 Block이 있다는 거
					avail = true;
					break;//하나라도 존재하면 바로 반복문을 빠져나온다.
				}
			}
			if(avail)//처음 반복문도 빠져나온다.
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
	 * 블럭의 값을 0으로
	 */
	public void removeCard(int position){
		block[position%12][position/12] = 0;
	}
	
	/*
	 * 전체 블럭의 크기(외벽포함)
	 */
	public int size(){
		return 156;
	}
	
	/*
	 * position으로 값을 리턴
	 */
	public int get(int position){
		return block[position%12][position/12];
	}
	
	/*
	 * x값과 y값으로 값을 리턴
	 */
	public int get(int x, int y){
		return block[x][y];
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
