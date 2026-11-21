/**
 * Minimal damped-spring integrator (no dependency). Drives values frame by frame with requestAnimationFrame:
 * a = (-stiffness * (x - target) - damping * v) / mass
 */
export interface SpringConfig {
  stiffness?: number;
  damping?: number;
  mass?: number;
  precision?: number;
}

export class Spring {
  private value: number;
  private velocity = 0;
  private target: number;
  private frame: number | null = null;
  private last = 0;
  private readonly stiffness: number;
  private readonly damping: number;
  private readonly mass: number;
  private readonly precision: number;

  constructor(initial: number, private readonly onUpdate: (value: number) => void, config: SpringConfig = {}) {
    this.value = initial;
    this.target = initial;
    this.stiffness = config.stiffness ?? 170;
    this.damping = config.damping ?? 22;
    this.mass = config.mass ?? 1;
    this.precision = config.precision ?? 0.01;
  }

  get current(): number {
    return this.value;
  }

  set(target: number): void {
    this.target = target;
    if (this.frame === null) {
      this.last = performance.now();
      this.frame = requestAnimationFrame(this.step);
    }
  }

  /** Jump without animation. */
  jump(value: number): void {
    this.stop();
    this.value = value;
    this.target = value;
    this.velocity = 0;
    this.onUpdate(value);
  }

  stop(): void {
    if (this.frame !== null) cancelAnimationFrame(this.frame);
    this.frame = null;
  }

  private step = (now: number) => {
    // Integrate in small fixed sub-steps for stability.
    let dt = Math.min(0.064, (now - this.last) / 1000);
    this.last = now;
    while (dt > 0) {
      const h = Math.min(dt, 1 / 240);
      const force = -this.stiffness * (this.value - this.target) - this.damping * this.velocity;
      this.velocity += (force / this.mass) * h;
      this.value += this.velocity * h;
      dt -= h;
    }
    const settled = Math.abs(this.velocity) < this.precision && Math.abs(this.value - this.target) < this.precision;
    if (settled) {
      this.value = this.target;
      this.velocity = 0;
      this.frame = null;
      this.onUpdate(this.value);
      return;
    }
    this.onUpdate(this.value);
    this.frame = requestAnimationFrame(this.step);
  };
}
