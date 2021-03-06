import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.*;

public class Fish extends Swimmable {
	private final int countEatToGrow = 4;
	private int size;
	private Color col;
	private int eatCount;
	private int x_front, y_front, x_dir, y_dir;
	private boolean isSuspended = false;
	private CyclicBarrier barrier = null;
	private PropertyChangeSupport support;
	private boolean supportFiredFlag = false;
	private int hungerFreq;
	private int hungerTick = 0;
	private HungerState hungerState;

	/**
	 * Creates a new fish
	 *
	 * @param size     as {@code int} to be given.
	 * @param x_front  as {@code int} to be given.
	 * @param y_front  as {@code int} to be given.
	 * @param horSpeed as {@code int} to be given.
	 * @param verSpeed as {@code int} to be given.
	 * @param col      as {@code int} to be given.
	 */
	public Fish(int size, int x_front, int y_front, int horSpeed, int verSpeed, Color col, int hungerFreq) {
		super(horSpeed, verSpeed);
		this.size = size;
		this.col = col;
		this.x_front = x_front;
		this.y_front = y_front;
		this.eatCount = 0;
		this.x_dir = 1;
		this.y_dir = 1;
		support = new PropertyChangeSupport(this);
		this.hungerFreq = hungerFreq;
		hungerState = new Satiated();
	}
	
	public Fish(Fish other) {
		super.horSpeed = other.horSpeed;
		super.verSpeed = other.verSpeed;
		this.size = other.size;
		this.col = other.col;
		this.x_front = other.x_front;
		this.y_front = other.y_front;
		this.eatCount = other.eatCount;
		this.x_dir = other.x_dir;
		this.y_dir = other.y_dir;
		this.isSuspended = other.isSuspended;
		this.barrier = other.barrier;
		this.support=other.support;
		this.supportFiredFlag = other.supportFiredFlag;
		this.hungerFreq=other.hungerFreq;
		this.hungerTick = other.hungerTick;
		this.hungerState=other.hungerState;
	}

	@Override
	public Fish clone(){  
		return new Fish(size, x_front, y_front, horSpeed, verSpeed, col, hungerFreq);
	}  

	/**
	 * @return String with the name of the animal
	 */
	public String getAnimalName() {
		return "Fish";
	}

	public void setSuspend() {
		isSuspended = true;
	}

	public void setResume() {
		synchronized (this) {
			isSuspended = false;
			notify();
		}
	}

	/**
	 * @return the fish size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * this function check if the fish eat enough to go bigger
	 * 
	 * @see changefish
	 */
	public void eatInc() {
		this.eatCount++;
		if (this.eatCount == this.countEatToGrow) {
			this.eatCount = 0;
			changeFish();
		}
	}

	/**
	 * @return int with the number of time the fish ate before he chage his size
	 */
	public int getEatCount() {
		return this.eatCount;
	}

	/**
	 * @return String with the color of the fish
	 */
	public String getColorName() {
		String rgb = String.valueOf(this.col.getRed()) + "," + String.valueOf(this.col.getGreen()) + ","
				+ String.valueOf(this.col.getBlue());
		if (rgb.equals("255,0,0"))
			return "Red";
		else if (rgb.equals("0,0,255"))
			return "Blue";
		else if (rgb.equals("0,255,0"))
			return "Green";
		else if (rgb.equals("0,255,255"))
			return "Cyan";
		else if (rgb.equals("255,165,0"))
			return "Orange";
		else if (rgb.equals("255,255,0"))
			return "Yellow";
		else if (rgb.equals("255,0,255"))
			return "Magenta";
		else if (rgb.equals("255,105,180"))
			return "Pink";
		else
			return rgb;
	}
	
	@Override
	public Color getColor() {
		return this.col;
	}
	
	@Override
	public void PaintFish(Color c) {
		this.col = c;
	}

	/**
	 * this function increse the size of the fish by one
	 */
	public void changeFish() {
		this.size++;
	}

	public void drawCreature(Graphics g) {
		g.setColor(col);
		if (x_dir == 1) // fish swims to right side
		{
			// Body of fish
			g.fillOval(x_front - size, y_front - size / 4, size, size / 2);

			// Tail of fish
			int[] x_t = { x_front - size - size / 4, x_front - size - size / 4, x_front - size };
			int[] y_t = { y_front - size / 4, y_front + size / 4, y_front };
			Polygon t = new Polygon(x_t, y_t, 3);
			g.fillPolygon(t);

			// Eye of fish
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue()));
			g2.fillOval(x_front - size / 5, y_front - size / 10, size / 10, size / 10);

			// Mouth of fish
			if (size > 70)
				g2.setStroke(new BasicStroke(3));
			else if (size > 30)
				g2.setStroke(new BasicStroke(2));
			else
				g2.setStroke(new BasicStroke(1));
			g2.drawLine(x_front, y_front, x_front - size / 10, y_front + size / 10);
			g2.setStroke(new BasicStroke(1));
		} else // fish swims to left side
		{
			// Body of fish
			g.fillOval(x_front, y_front - size / 4, size, size / 2);

			// Tail of fish
			int[] x_t = { x_front + size + size / 4, x_front + size + size / 4, x_front + size };
			int[] y_t = { y_front - size / 4, y_front + size / 4, y_front };
			Polygon t = new Polygon(x_t, y_t, 3);
			g.fillPolygon(t);

			// Eye of fish
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue()));
			g2.fillOval(x_front + size / 10, y_front - size / 10, size / 10, size / 10);

			// Mouth of fish
			if (size > 70)
				g2.setStroke(new BasicStroke(3));
			else if (size > 30)
				g2.setStroke(new BasicStroke(2));
			else
				g2.setStroke(new BasicStroke(1));
			g2.drawLine(x_front, y_front, x_front + size / 10, y_front + size / 10);
			g2.setStroke(new BasicStroke(1));
		}
	}

	public void setBarrier(CyclicBarrier b) {
		this.barrier = b;
	}

	public void run() {

		try {
			Thread.sleep(100); // slow the fish down
			if (isSuspended) {
				if (AquaPanel.wormInstance != null) {
					if (barrier != null)
						barrier.await();
					synchronized (this) {
						wait();
					}
				}
			} else {
					synchronized (this) {
						if(hungerTick >= hungerFreq && supportFiredFlag == false) {
						hungerState = new Hungry();
						support.firePropertyChange("hungerState", this.hungerState instanceof Satiated, this.hungerState instanceof Hungry);
						supportFiredFlag = true;
					}
				}
				if (hungerState instanceof Hungry && AquaPanel.wormInstance != null)
					eatWorm();
				else
					move();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}

		AquaFrame.panel.repaint();
	}

	public void move() {

		this.x_front += horSpeed * x_dir;
		this.y_front += verSpeed * y_dir;
		if (x_front >= AquaFrame.panel.getWidth()) {
			x_dir = -1;
			if(hungerState instanceof Satiated) hungerTick++;
		}
		if (y_front >= AquaFrame.panel.getHeight()) {
			y_dir = -1;
			if(hungerState instanceof Satiated) hungerTick++;
		}
		if (x_front <= 0) {
			x_dir = 1;
			if(hungerState instanceof Satiated) hungerTick++;
		}
		if (y_front <= 0) {
			y_dir = 1;
			if(hungerState instanceof Satiated) hungerTick++;
		}
	}

	public void eatWorm() {
		if (barrier != null) {
			try {
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}

		// Calculate path to worm
		double v_old = Math.sqrt(horSpeed * horSpeed + verSpeed * verSpeed);
		double k = (Math.abs((double) y_front - (double) (AquaFrame.panel.getHeight()) / 2)
				/ Math.abs((double) x_front - (double) (AquaFrame.panel.getWidth()) / 2));
		double newHorSpeed = v_old / Math.sqrt(k * k + 1);
		double newVerSpeed = newHorSpeed * k;

		x_front += newHorSpeed * x_dir;
		y_front += newVerSpeed * y_dir;
		if (x_front >= AquaFrame.panel.getWidth() / 2)
			x_dir = -1;
		else
			x_dir = 1;
		if (y_front > AquaFrame.panel.getHeight() / 2)
			y_dir = -1;
		else
			y_dir = 1;

		synchronized (this) {
			// If fish is 5 pixels away from the worm
			if ((Math.abs(AquaFrame.panel.getWidth() / 2 - x_front) <= 5)
					&& (Math.abs(AquaFrame.panel.getHeight() / 2 - y_front) <= 5)) {
				barrier = null;
				hungerTick = 0;
				hungerState = new Satiated();
				supportFiredFlag = false;
				AquaFrame.panel.wormEatenBy(this);
				Singleton.set();
				AquaPanel.wormInstance = null;
				AquaFrame.btnFood.setEnabled(true);
				AquaFrame.panel.repaint();
				AquaFrame.updateJTable();
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public int getHungerFreq() {
		return hungerFreq;
	}

	public void setHungerState(HungerState state) {
		hungerState = state;
	}

	public HungerState getHungerState() {
		return hungerState;
	}

	@Override
	public int getXpos() {
		return x_front;
	}

	@Override
	public int getYpos() {
		return y_front;
	}

	@Override
	public void setState(int size, int x_front, int y_front, int horSpeed, int verSpeed, Color col) {
		super.horSpeed = horSpeed;
		super.verSpeed = verSpeed;
		this.size = size;
		this.col = col;
		this.x_front = x_front;
		this.y_front = y_front;
	}
}
