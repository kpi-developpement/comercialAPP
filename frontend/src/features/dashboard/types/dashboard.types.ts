export interface Point {
  label: string;
  valeur: number;
}

export interface Dashboard {
  dateDebut: string;
  dateFin: string;
  kpis: {
    chiffreAffairesHt: number;
    margeBrute: number;
    tauxMarge: number;
    nombreFactures: number;
    totalImpayes: number;
    aEncaisserSemaine: number;
  };
  caParMois: Point[];
  topClients: Point[];
  topArticles: Point[];
  impayesParAge: Point[];
}
