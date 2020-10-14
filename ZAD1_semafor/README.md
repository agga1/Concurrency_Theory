**Zad 1.**  
Implementacja semafora binarnego: BinarySemaphore  
Synchronizacja programu wyścig: Task1  


**Zad 2. Dlaczego if nie wystarczy?**    
Potrzebna jest instrukcja while, ponieważ może się zdarzyć, że wątek został obudzony,    
a dany warunek nadal nie jest spełniony, na przykład:   
-jest conajmniej 2 konsumentów, 1 producent, oraz wspólny monitor z kolejką.   
-Konsumer1 dostał monitor z kolejką, ale jest pusta więc czeka i zwalnia monitor.  
-Producent zaczyna tworzyć produkt, w tym czasie pojawia się drugi konsument, który  
zaczyna czekać na monitor kolejki.  
-Producent zwalnia monitor kolejki, który następnie dostaje Konsumer2 (może się tak zdarzyć, nie musi).  
-Kolejka nie jest pusta, więc konsumer2 zabiera produkt i zwalnia monitor.  
-Konsumer1 dostaje monitor i kontynuuje działanie - nie sprawdzając ponownie czy kolejka jest pusta.  

2 przyklady wyścigu przy użyciu if zamiast while:    
- z monitorem i czekaniem opisany powyżej - czasami zwraca NoSuchElementException
- 2 liczniki z semaforem binarnym używajacym 'if' - problem gdy wątek czeka w wielu kolejkach,    
więc nie wiadomo która kolejka wywołała notify() - liczniki zwykle są przesunięte o pare wartości  


**Zad. 3**  
semafor licznikowy: Semaphore  
przykład użycia: Task3  

Semafor binarny nie jest szególnym przypadkiem semafora licznikowego, gdyż nie pamięta on, ile razy została wykonana na nim    
operacja P() - nie spełnia więc definicji semafora licznikowego. 
Semafor licznikowy z licznikiem 1 moze natomiast zostać użyty w roli semafora binarnego.

