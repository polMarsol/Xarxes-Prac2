# Pràctica 2 - XARXES

## Realització de la Pràctica

- Pràctica realitzada en grup (2 integrants)

## Integrants

  - Hugo Fernández
  - Pol Marsol

## Estratègia d'Implementació

El codi implementa un servidor i un client que permeten gestionar una base de dades de llibres. 

El servidor es posa en marxa i espera les connexions dels clients. Quan un client es connecta, el servidor crea un nou fil d'execució per gestionar la comunicació amb aquest client. 

El client pot enviar diferents opcions al servidor: 

1. Llistar tots els títols de llibres en la base de dades.
2. Obtenir la informació d'un llibre específic.
3. Afegir un nou llibre a la base de dades.
4. Eliminar un llibre de la base de dades.
5. Desconnectar-se del servidor.

Quan el servidor rep una d'aquestes opcions, executa l'acció corresponent. Per exemple, si rep l'opció 1, llista tots els títols de llibres en la base de dades i els envia al client. Si rep l'opció 3, llegeix la informació del nou llibre del client i l'afegeix a la base de dades.

Per a garantir la consistència de la base de dades quan diversos clients intenten modificar-la al mateix temps, el servidor utilitza un mecanisme de sincronització. Això assegura que només un fil d'execució pot modificar la base de dades a la vegada.

Finalment, quan un client decideix desconnectar-se, el servidor tanca la connexió amb aquest client i el fil d'execució que gestionava la comunicació amb aquest client finalitza.

## Estratègia de Comunicació Client-Servidor

- Tipus d'estratègia utilitzada: Concurrent
