---
title: Integration into Camunda BPM: Camunda Spin
---

## Camunda Spin

If the FEEL engine is used in the context of Camunda BPM then it includes support for [Camunda Spin](https://docs.camunda.org/manual/7.8/user-guide/data-formats/). That means variables of type Spin JSON or Spin XML are transformed into a context automatically. No need to use the Spin API explicitly. 

### Spin JSON

Variables of type Spin JSON are transformed into an equal context. No Transformation happens - it's basically the JSON structure. 

Spin JSON:

```json
{ 
  "name": "Kermit", 
  "address": 
  {
    "city": "Berlin", 
    "zip-code": 10961
  }
}
```

FEEL context:

```js
{ 
  name : "Kermit",
  address : 
  {
    city : "Berlin",
    zipCode : 10961
  }
}
```

### Spin XML

Variables of type Spin JSON are transformed into context applying the following rules:

* every XML element is a context entry
* every XML attritute is a context entry with prefix `@` under the element's entry
* multiple XML elements with the same are grouped in the context as list 
* the inner content of an XML element is set as context entry `$content`   
* if the element or the attribute has a namespace then the context entry has the prefix `<NAMESPACE>$`

Spin XML:

```xml
<customer name="Kermit">
  <address city="Berlin" zipCode="10961" />
</customer>
```

FEEL context:

```
{ 
  customer : 
  {
    @name : "Kermit",
    address : 
    {
      @city : "Berlin",
      @zipCode : "10961"
    }
  }
}
```
