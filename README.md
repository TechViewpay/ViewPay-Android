# ViewPay-Android : Guide de démarrage

Ce document a pour objectif de vous guider dans la mise en place du SDK ViewPay dans votre application Android en natif.
Pour rappel, Viewpay est une solution de micro-paiement par l'attention publicitaire, qui permet à l'utilisateur de débloquer un contenu premium en regardant une publicité. Viewpay a pour vocation d'être une alternative à d'autres options pour débloquer un contenu premium, et ne doit pas être installé comme seule option d'un paywall.

Voici un exemple de déblocage d'article avec Viewpay : 

![sample](https://github.com/TechViewpay/ViewPay-iOS/blob/master/DocImages/parcours_vp_mobile3.png?raw=true)

## Prérequis

- Utiliser Android Studio
- Compatibilité android 4.0.3  (API 15) ou version ultérieure

## Installation du SDK

- Téléchargez la dernière version du SDK via le lien suivant : [ViewPay.zip](https://github.com/TechViewpay/ViewPay-Android/blob/master/Dist/ViewPay.zip?raw=true)
- Décompressez l'archive
-	Copier le fichier "viewpay.aar" dans le répertoire "libs" de votre projet.
- Ajouter l'extrait de code `flatDir{dirs 'libs'}` à la section "allprojects" du fichier "build.gradle" (Project) :
```java
allprojects {    
	repositories {       
		jcenter()       
		flatDir {        
			dirs 'libs'      
		}    
	} 
}
```

- Ajouter l'extrait de code `compile(name:'viewpay', ext:'aar')` à la section "dependencies" du fichier "build.gradle" (module:app) :
```java
dependencies {        
	compile(name:'viewpay', ext:'aar') 
}
```

- Synchroniser le projet pour mettre à jour les modifications.
-	Ajouter les permissions suivantes dans AndroidManifest.xml :
```java
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

## Initialisation du SDK
Pour que le SDK puisse fonctionner, vous devez l'initialiser aussi tôt que possible dans le cycle de vie de votre application.

- Commencez tout d'abord par importer le SDK dans l'entête de votre "Activity" avant le début de la déclaration de la class:

```java
import com.markelys.viewpay.ViewPay;
import com.markelys.viewpay.ViewPayEventsListener;
```

- Ensuite, dans la méthode `onCreate(Bundle savedInstanceState)`, initialisez le SDK avec l'identifiant qui vous a été fourni par ViewPay de la manière suivante:

```java
ViewPay.init("votre activity".this,"<votre_ACCOUNT_ID>");  
```

-	Vous devez maintenant implémenter sur l’activité en cours l'interface ViewPayEventsListener. Cette interface possède 5 méthodes:

```java
public void checkVideoSuccesVP();
public void checkVideoErrorVP();
public void errorVP();
public void closeAdsVP();
public void completeAdsVP();
```

## Vérifier la disponibilité de campagnes

Une fois le SDK initialisé et l'interface ViewPayEventsListener implémenté, la première étape dans l'utilisation du SDK est de vérifier au moment opportun la disponibilité d'une ou plusieurs campagnes
Pour cela, vous devez appeler la méthode 

```java
ViewPay.checkVideo();
```

Si le checkVideo retourne un succès, alors la methode checkVideoSuccesVP() est appelée et vous pouvez mettre à jour l'interface (affichage bouton par exemple) pour proposer à l'utilisateur de "payer" la transaction en visionnant une vidéo. 
Si le checkVideo retourne un erreur, alors c'est la methode checkVideoErrorVP() qui est appelée et vous pouvez faire le traitement correspondante.

```java
@Override
public void checkVideoSuccesVP() {
    //Traitement si le checkVideo retourne succès	
}

@Override
public void checkVideoErrorVP() {
    //Traitement si le checkVideo retourne un erreur
}
```

## Présenter la publicité

A partir du résultat de l'appel du checkVideo, vous êtes en mesure de savoir si des publicités sont disponibles pour valider la transaction via ViewPay.
Lorsque l'utilisateur va choisir l'option Viewpay en cliquant sur le bouton dans le paywall, l'étape suivante consiste donc à présenter l'AdSelector ViewPay à l'utilisateur pour lui permettre de choisir et visionner une publicité video.
Pour cela, vous devez appeler la méthode 

```java
ViewPay.presentAd ();
```

En fonction des évènements qui se passent au niveau de l'AdSelector, trois méthodes peuvent être appelées :

```java
@Override
public void errorVP() {
    // Traitement en cas d'erreur dans le parcours Viewpay
}

@Override
public void closeAdsVP() {
    //Traitement quand l'utilisateur ferme Viewpay
}

@Override
public void completeAdsVP() {
    //Traitement quand l'utilisateur clique sur le bouton "Access your content"
}
```

## Métadonnées et customisation

Le SDK ViewPay remonte un certain nombre de meta données pour permettre une meilleure catégorisation des publicités présentées à l'utilisateur.
Si ces informations sont disponibles dans votre application vous pouvez facilement renseigner à partir des méthodes suivantes:

```java
ViewPay.setUserGender("_genre_") 
ViewPay.setUserAge(_age_) 
ViewPay.setCountry("_pays_") 
ViewPay.setLanguage("_language_") 
ViewPay.setPostalCode("_code_postal_") 
ViewPay.setCategorie("_categorie_")
```

NB: l'appel des ces méthodes doit être fait après l'initialisation et avant le checkVideo.
