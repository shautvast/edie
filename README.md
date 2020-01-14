This project is called Edie after Edie Sedgwick who was one of the artists in Andy Warhols Factory. 
The film about her life is called Factory Girl (2006). 
This project was inspired by the FactoryGirl project that was later renamed FactoryBot. 


* https://en.wikipedia.org/wiki/Edie_Sedgwick
* https://www.imdb.com/title/tt0432402
* https://hn.algolia.com/?q=factorybot

*Usage:*
for a java bean like:
```java
class Person{
    String name;
    int age;
    
    //rest omitted for brevity
}
```
* Create a definition (template for creating instances) like so:
```
Factory.define(Person.class, () -> new Person("John", 48));
```
* Create instances like so:
```
Person person = Factory.build(Person.class)
```

* But the real _power_ of this lies in
```
Person person = Factory.build(Person.class, p -> p.setAge(25));
```
This will result in an object 
```json 
{name: "John", age: 25}
```

Meaning the template can be customized later on
