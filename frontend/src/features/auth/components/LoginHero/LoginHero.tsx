import styles from "./LoginHero.module.css";

const FEATURES = ["Ventes & facturation", "Stock multi-dépôts · PUMP", "Règlements & échéancier", "Tableau de bord temps réel"];

/** Left side of the login screen: morphing emblem, animated headline and feature chips. */
export function LoginHero() {
  return (
    <section className={styles.hero} aria-label="Présentation">
      <div className={styles.emblem} aria-hidden>
        <span className={styles.ring} />
        <span className={styles.ring2} />
        <span className={styles.core} />
      </div>
      <p className={styles.kicker}>Gestion commerciale · Maroc</p>
      <h2 className={styles.headline}>
        <span>Pilotez votre entreprise</span>
        <span className={styles.gradient}>à la vitesse de la lumière.</span>
      </h2>
      <p className={styles.lead}>Devis, livraisons, factures, stock et encaissements réunis dans un seul cockpit.</p>
      <ul className={styles.chips}>
        {FEATURES.map((f, i) => (
          <li key={f} style={{ "--i": i } as React.CSSProperties}>{f}</li>
        ))}
      </ul>
    </section>
  );
}
