package org.zapto.mike;

import java.util.Random;

public class TunnelHandler {

	private static int leftBound;
	private static int rightBound;
	
	public static void main(String[] args) {
		TunnelQueue tq = new TunnelQueue();
		Random r = new Random();
		leftBound = -2;
		rightBound = -1;
		CarSender temp;
		/*
		 * This loop creates and sends cars randomly
		 */
		while(true) {
			try {
				boolean tempbool = r.nextBoolean();
				if(tempbool) {
					leftBound += 2;
					temp = new CarSender(tq, new Car(tempbool, leftBound));
				} else {
					rightBound += 2;
					temp = new CarSender(tq, new Car(tempbool, rightBound));
				}
				new Thread(temp).start();
				Thread.sleep((long)(Math.random() * 5000));
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class TunnelQueue {
	
	private boolean currentDirection;
	private boolean waiting;
	private int currentInTunnel;
	private final int tunnelTravelTime = 2500;
	private final int tunnelWaitTime = 5000;
	
	public void enQueue(Car car) throws InterruptedException{
		System.out.println(car + " wants to enter the tunnel");
		/*
		 * See if there is a car in the tunnel
		 */
		if(currentInTunnel > 0) {
			/*
			 * If there is then see if it is going the direction you are and if there are any pending direction changes
			 */
			while(waiting) {
				Thread.sleep(100);
			}
			if(currentDirection == car.bound ) {
				/*
				 * If it is going in the same direction then enter the tunnel 
				 */
				currentInTunnel++;
				System.out.println(car.getEnterSuffix() + car + " entered the tunnel");
				Thread.sleep(tunnelTravelTime);
				System.out.println(car.getExitSuffix() + car + " is exiting the tunnel");
				currentInTunnel--;
			} else {
				/*
				 * If it is not then wait until your wait time has expired or the there are no cars heading in your direction
				 */
				long time = System.currentTimeMillis();
				while(currentInTunnel > 0 && System.currentTimeMillis() - time < tunnelWaitTime && currentDirection != car.bound ) {
					Thread.sleep(100);
				}
				/*
				 * After your 5 second wait time or the tunnel is clear set the current direction to your bound and let any new cars know to wait for the tunnel to clear
				 */
				if(currentDirection != car.bound) {
					currentDirection = car.bound;
					waiting = true;
					/*
					 * Check if the tunnel is clear if not wait until it is
					 */
					while(currentInTunnel > 0) {
						Thread.sleep(100);
					}
					waiting = false;
				} else {
					/*
					 * If the tunnel is set to your direction check if any cars are still in the tunnel heading in the opposite direction =
					 */
					while(waiting) {
						Thread.sleep(100);
					}
				}
				/*
				 * Enter the tunnel
				 */
				currentInTunnel++;
				System.out.println(car.getEnterSuffix() + car + " entered the tunnel");
				Thread.sleep(tunnelTravelTime);
				System.out.println(car.getExitSuffix() + car + " is exiting the tunnel");
				currentInTunnel--;
			}
		} else {
			/*
			 * If there is not then enter the tunnel and mark the time you entered
			 */
			currentDirection = car.bound;
			currentInTunnel++;
			System.out.println(car.getEnterSuffix() + car + " entered the tunnel");
			Thread.sleep(tunnelTravelTime);
			System.out.println(car.getExitSuffix() + car + " is exiting the tunnel");
			currentInTunnel--;
		}
	}
}

class Car {
	
	boolean bound;
	int id;
	
	public Car(boolean bound, int id) {
		this.bound = bound;
		this.id = id;
	}
	
	public String getEnterSuffix() {
		return bound ? "   |¯|<--   " : "   -->|¯|   " ;
	}
	
	public String getExitSuffix() {
		return bound ? "   <--|¯|   " : "   |¯|-->   " ;
	}
	
	@Override 
	public String toString() {
		return bound ? "Left-Bound Car " + id : "Right-Bound Car " + id;
	}
}

class CarSender implements Runnable{
	
	private TunnelQueue tq;
	private Car car;
	
	public CarSender(TunnelQueue tq, Car car) {
		this.tq = tq;
		this.car = car;
	}
	
	@Override
	public void run() {
		try {
			tq.enQueue(car);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}