# Quiz en images 📱

Application Android développée en Kotlin natif avec une interface XML, conçue dans le cadre du TP « Quiz en images ».

## Contraintes techniques respectées
- **Langage** : Kotlin uniquement.
- **Architecture** : Une seule activité (`MainActivity`), aucun fragment.
- **Interface** : Fichiers de ressources XML (`activity_main.xml`), aucun Jetpack Compose.
- **Données** : 5 questions statiques en mémoire (aucune base de données ni réseau).
- **Images** : Ressources vectorielles dans `res/drawable` (`triangle`, `carre`, `cercle`, `etoile`, `rectangle`).
- **Accessibilité & Ergonomie** : `contentDescription` sur chaque image, messages textuels clairs pour les retours (sans dépendre uniquement de la couleur).
- **Rotation & États** : Sauvegarde et restauration de l'état avec `onSaveInstanceState`.

## Auteur
- Développeur : Ajala Ghassen
- Dépôt distant : [https://github.com/AjalaGhassen/quizz.git](https://github.com/AjalaGhassen/quizz.git)
