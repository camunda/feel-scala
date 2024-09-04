"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[7010],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>f});var a=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function r(e,t){if(null==e)return{};var n,a,o=function(e,t){if(null==e)return{};var n,a,o={},l=Object.keys(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var s=a.createContext({}),u=function(e){var t=a.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=u(e.components);return a.createElement(s.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},m=a.forwardRef((function(e,t){var n=e.components,o=e.mdxType,l=e.originalType,s=e.parentName,c=r(e,["components","mdxType","originalType","parentName"]),m=u(n),f=o,d=m["".concat(s,".").concat(f)]||m[f]||p[f]||l;return n?a.createElement(d,i(i({ref:t},c),{},{components:n})):a.createElement(d,i({ref:t},c))}));function f(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var l=n.length,i=new Array(l);i[0]=m;var r={};for(var s in t)hasOwnProperty.call(t,s)&&(r[s]=t[s]);r.originalType=e,r.mdxType="string"==typeof e?e:o,i[1]=r;for(var u=2;u<l;u++)i[u]=n[u];return a.createElement.apply(null,i)}return a.createElement.apply(null,n)}m.displayName="MDXCreateElement"},7125:(e,t,n)=>{n.d(t,{Z:()=>o});var a=n(7294);const o=e=>{let{versionZeebe:t,versionC7:n}=e;return a.createElement("p",null,a.createElement("span",{style:{backgroundColor:"#26D07C",borderRadius:"7px",color:"#fff",padding:"0.2rem",marginRight:"0.5rem"},title:"Available since the given Camunda Platform 8 (Zeebe) version."},"Zeebe: ",t),a.createElement("span",{style:{backgroundColor:"#0072CE",borderRadius:"7px",color:"#fff",padding:"0.2rem"},title:"Available since the given Camunda Platform 7 version."},"Camunda Platform: ",n))}},4017:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>r,default:()=>m,frontMatter:()=>i,metadata:()=>s,toc:()=>c});var a=n(7462),o=(n(7294),n(3905)),l=n(7125);const i={id:"changelog",title:"Changelog",slug:"/changelog/"},r=void 0,s={unversionedId:"changelog/changelog",id:"version-1.17/changelog/changelog",title:"Changelog",description:"This page contains an overview of the released versions and highlights the major changes from a user",source:"@site/versioned_docs/version-1.17/changelog/changelog.md",sourceDirName:"changelog",slug:"/changelog/",permalink:"/feel-scala/docs/1.17/changelog/",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/versioned_docs/version-1.17/changelog/changelog.md",tags:[],version:"1.17",frontMatter:{id:"changelog",title:"Changelog",slug:"/changelog/"},sidebar:"Changelog"},u={},c=[{value:"1.17",id:"117",level:2},{value:"1.16",id:"116",level:2},{value:"1.15",id:"115",level:2},{value:"1.14",id:"114",level:2},{value:"1.13",id:"113",level:2},{value:"1.12",id:"112",level:2},{value:"1.11",id:"111",level:2}],p={toc:c};function m(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,a.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"This page contains an overview of the released versions and highlights the major changes from a user\npoint of view (i.e. focus on features). The complete changelog, including the patch\nversions, can be found on the ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases"},"GitHub release page"),"."),(0,o.kt)("h2",{id:"117"},"1.17"),(0,o.kt)(l.Z,{versionZeebe:"8.3.0",versionC7:"7.21.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Expressions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"Overhauled error handling. Instead of failing the evaluation, for example, because of a non-existing\nvariable or context entry, it handles these cases and returns ",(0,o.kt)("inlineCode",{parentName:"li"},"null"),". ")),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Built-in functions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"New built-in\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-list#duplicate-valueslist"},"duplicate values()"),"\nto find duplicate list items"),(0,o.kt)("li",{parentName:"ul"},"New built-in\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-boolean#get-or-elsevalue-default"},"get or else()"),"\nto handle ",(0,o.kt)("inlineCode",{parentName:"li"},"null")," values"),(0,o.kt)("li",{parentName:"ul"},"New built-in\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-boolean#assertvalue-condition"},"assert()"),"\nto fail the evaluation if a condition is not met")),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.17.0"},"here"),"."),(0,o.kt)("h2",{id:"116"},"1.16"),(0,o.kt)(l.Z,{versionZeebe:"8.2.0",versionC7:"7.20.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Built-in functions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"New built-in function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#get-valuecontext-keys"},"get value()")," to access a context with a dynamic path"),(0,o.kt)("li",{parentName:"ul"},"New built-in function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#context-putcontext-keys-value"},"context put()")," to insert a nested value in a context"),(0,o.kt)("li",{parentName:"ul"},"New built-in function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#last-day-of-monthdate"},"last day of month()")," to get the last day of a month"),(0,o.kt)("li",{parentName:"ul"},"New built-in function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-conversion#date-and-timedate-timezone"},"date and time()")," to get a date-time for a timezone"),(0,o.kt)("li",{parentName:"ul"},"New built-in function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#random-number"},"random number()")," to get a random number")),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.16.0"},"here"),"."),(0,o.kt)("h2",{id:"115"},"1.15"),(0,o.kt)(l.Z,{versionZeebe:"8.1.0",versionC7:"7.19.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Expressions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"New ",(0,o.kt)("inlineCode",{parentName:"li"},"@")," notation for ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions#literal"},"temporal literals"))),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.15.0"},"here"),"."),(0,o.kt)("h2",{id:"114"},"1.14"),(0,o.kt)(l.Z,{versionZeebe:"1.3.1",versionC7:"7.18.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Built-in functions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"New function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-string#extractstring-pattern"},"extract()"),"\nthat applies a regular expression to a given a string"),(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-list#string-joinlist"},"string join()"),"\nthat merges a list of strings into a single string"),(0,o.kt)("li",{parentName:"ul"},"New ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-range"},"range functions")," to compare\nranges and scalar values"),(0,o.kt)("li",{parentName:"ul"},"New functions to round numeric values:",(0,o.kt)("ul",{parentName:"li"},(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-upn-scale"},"round up()")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-downn-scale"},"round down()")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-half-upn-scale"},"round half up()")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-half-downn-scale"},"round half down()")))),(0,o.kt)("li",{parentName:"ul"},"Extend function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#absn"},"abs()")," for\nduration values")),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.14.0"},"here"),"."),(0,o.kt)("h2",{id:"113"},"1.13"),(0,o.kt)(l.Z,{versionZeebe:"1.0.0",versionC7:"7.15.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Expressions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"Access the property ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions#properties"},"weekday"),"\nof date and date-time values"),(0,o.kt)("li",{parentName:"ul"},"Allow escape sequences in ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-data-types#string"},"string literals"))),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Built-in functions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-conversion#contextentries"},"context()"),"\nthat creates a context from a given key-value list"),(0,o.kt)("li",{parentName:"ul"},"New function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#context-putcontext-key-value"},"put()")," that\nextends a context by a given entry"),(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#context-mergecontexts"},"put all()"),"\nthat merges the given contexts")),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.13.0"},"here"),"."),(0,o.kt)("h2",{id:"112"},"1.12"),(0,o.kt)(l.Z,{versionZeebe:"0.25.0",versionC7:"7.14.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Built-in functions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"New function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#now"},"now()")," that\nreturns the current date-time"),(0,o.kt)("li",{parentName:"ul"},"New function ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#today"},"today()"),"\nthat returns the current date"),(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#week-of-yeardate"},"week of year()"),"\nthat returns the number of the week within the year"),(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#month-of-yeardate"},"month of year()"),"\nthat returns the name of the month"),(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#day-of-weekdate"},"day of week()"),"\nthat returns name of the weekday"),(0,o.kt)("li",{parentName:"ul"},"New\nfunction ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#day-of-yeardate"},"day of year()"),"\nthat returns the number of the day within the year")),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.12.0"},"here"),"."),(0,o.kt)("h2",{id:"111"},"1.11"),(0,o.kt)(l.Z,{versionZeebe:"0.23.0",versionC7:"7.13.0",mdxType:"MarkerChangelogVersion"}),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Expressions:")),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"Access the ",(0,o.kt)("a",{parentName:"li",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-list-expressions#get-element"},"element of a list")," using a numeric variable"),(0,o.kt)("li",{parentName:"ul"},"Disable external functions by default for security reasons")),(0,o.kt)("p",null,"See the full changelog ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/camunda/feel-scala/releases/tag/1.11.0"},"here"),"."))}m.isMDXComponent=!0}}]);