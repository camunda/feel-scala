"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[8535],{3905:(e,n,a)=>{a.d(n,{Zo:()=>c,kt:()=>u});var t=a(7294);function r(e,n,a){return n in e?Object.defineProperty(e,n,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[n]=a,e}function i(e,n){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);n&&(t=t.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),a.push.apply(a,t)}return a}function l(e){for(var n=1;n<arguments.length;n++){var a=null!=arguments[n]?arguments[n]:{};n%2?i(Object(a),!0).forEach((function(n){r(e,n,a[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(a,n))}))}return e}function o(e,n){if(null==e)return{};var a,t,r=function(e,n){if(null==e)return{};var a,t,r={},i=Object.keys(e);for(t=0;t<i.length;t++)a=i[t],n.indexOf(a)>=0||(r[a]=e[a]);return r}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(t=0;t<i.length;t++)a=i[t],n.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var s=t.createContext({}),p=function(e){var n=t.useContext(s),a=n;return e&&(a="function"==typeof e?e(n):l(l({},n),e)),a},c=function(e){var n=p(e.components);return t.createElement(s.Provider,{value:n},e.children)},m={inlineCode:"code",wrapper:function(e){var n=e.children;return t.createElement(t.Fragment,{},n)}},d=t.forwardRef((function(e,n){var a=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,c=o(e,["components","mdxType","originalType","parentName"]),d=p(a),u=r,f=d["".concat(s,".").concat(u)]||d[u]||m[u]||i;return a?t.createElement(f,l(l({ref:n},c),{},{components:a})):t.createElement(f,l({ref:n},c))}));function u(e,n){var a=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var i=a.length,l=new Array(i);l[0]=d;var o={};for(var s in n)hasOwnProperty.call(n,s)&&(o[s]=n[s]);o.originalType=e,o.mdxType="string"==typeof e?e:r,l[1]=o;for(var p=2;p<i;p++)l[p]=a[p];return t.createElement.apply(null,l)}return t.createElement.apply(null,a)}d.displayName="MDXCreateElement"},3242:(e,n,a)=>{a.d(n,{Z:()=>r});var t=a(7294);const r=()=>t.createElement("p",null,t.createElement("span",{style:{backgroundColor:"#FC5D0D",borderRadius:"7px",color:"#fff",padding:"0.2rem",marginRight:"0.5rem"},title:"This feature is not part of the official DMN standard. It is an extension from Camunda's implementation."},"Camunda Extension"))},1475:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>p,contentTitle:()=>o,default:()=>d,frontMatter:()=>l,metadata:()=>s,toc:()=>c});var t=a(7462),r=(a(7294),a(3905)),i=a(3242);const l={id:"feel-variables",title:"Variables",description:"This document outlines variables and examples."},o=void 0,s={unversionedId:"reference/language-guide/feel-variables",id:"reference/language-guide/feel-variables",title:"Variables",description:"This document outlines variables and examples.",source:"@site/docs/reference/language-guide/feel-variables.md",sourceDirName:"reference/language-guide",slug:"/reference/language-guide/feel-variables",permalink:"/feel-scala/docs/next/reference/language-guide/feel-variables",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/docs/reference/language-guide/feel-variables.md",tags:[],version:"current",frontMatter:{id:"feel-variables",title:"Variables",description:"This document outlines variables and examples."},sidebar:"Reference",previous:{title:"Temporal expressions",permalink:"/feel-scala/docs/next/reference/language-guide/feel-temporal-expressions"},next:{title:"Control flow",permalink:"/feel-scala/docs/next/reference/language-guide/feel-control-flow"}},p={},c=[{value:"Access variables",id:"access-variables",level:3},{value:"Variable names",id:"variable-names",level:3},{value:"Escape variable names",id:"escape-variable-names",level:3}],m={toc:c};function d(e){let{components:n,...a}=e;return(0,r.kt)("wrapper",(0,t.Z)({},m,a,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("h3",{id:"access-variables"},"Access variables"),(0,r.kt)("p",null,"Access the value of a variable by its variable name."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"a + b\n")),(0,r.kt)("p",null,"If the value of the variable is a context, a ",(0,r.kt)("a",{parentName:"p",href:"feel-context-expressions#get-entrypath"},"context entry can be accessed")," by its key. "),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"a.b\n")),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"Use a ",(0,r.kt)("a",{parentName:"p",href:"feel-boolean-expressions#null-check"},"null-check")," if the variable can be ",(0,r.kt)("inlineCode",{parentName:"p"},"null")," or is optional."),(0,r.kt)("pre",{parentName:"admonition"},(0,r.kt)("code",{parentName:"pre",className:"language-js"},"a != null and a.b > 10 \n"))),(0,r.kt)("h3",{id:"variable-names"},"Variable names"),(0,r.kt)("p",null,"The name of a variable can be any alphanumeric string including the ",(0,r.kt)("inlineCode",{parentName:"p"},"_")," symbol. For a combination of\nwords, it's recommended to use the ",(0,r.kt)("inlineCode",{parentName:"p"},"camelCase")," or the ",(0,r.kt)("inlineCode",{parentName:"p"},"snake_case")," format. The ",(0,r.kt)("inlineCode",{parentName:"p"},"kebab-case")," format\nis not allowed because it contains the operator ",(0,r.kt)("inlineCode",{parentName:"p"},"-"),"."),(0,r.kt)("p",null,"When accessing a variable in an expression, keep in mind the variable name is case-sensitive."),(0,r.kt)("p",null,"Restrictions of a variable name:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"It may not start with a ",(0,r.kt)("em",{parentName:"li"},"number")," (e.g. ",(0,r.kt)("inlineCode",{parentName:"li"},"1stChoice")," is not allowed; you can\nuse ",(0,r.kt)("inlineCode",{parentName:"li"},"firstChoice")," instead)."),(0,r.kt)("li",{parentName:"ul"},"It may not contain ",(0,r.kt)("em",{parentName:"li"},"whitespaces")," (e.g. ",(0,r.kt)("inlineCode",{parentName:"li"},"order number")," is not allowed; you can use ",(0,r.kt)("inlineCode",{parentName:"li"},"orderNumber"),"\ninstead)."),(0,r.kt)("li",{parentName:"ul"},"It may not contain an ",(0,r.kt)("em",{parentName:"li"},"operator")," (e.g. ",(0,r.kt)("inlineCode",{parentName:"li"},"+"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"-"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"*"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"/"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"="),", ",(0,r.kt)("inlineCode",{parentName:"li"},">"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"<"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"?"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"."),")."),(0,r.kt)("li",{parentName:"ul"},"It may not be a ",(0,r.kt)("em",{parentName:"li"},"literal")," (e.g. ",(0,r.kt)("inlineCode",{parentName:"li"},"null"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"true"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"false"),") or a ",(0,r.kt)("em",{parentName:"li"},"keyword")," (e.g. ",(0,r.kt)("inlineCode",{parentName:"li"},"function"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"if"),"\n, ",(0,r.kt)("inlineCode",{parentName:"li"},"then"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"else"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"for"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"return"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"between"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"instance"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"of"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"not"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"in"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"and"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"or"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"some"),",\n",(0,r.kt)("inlineCode",{parentName:"li"},"every"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"satisfies"),").")),(0,r.kt)("h3",{id:"escape-variable-names"},"Escape variable names"),(0,r.kt)(i.Z,{mdxType:"MarkerCamundaExtension"}),(0,r.kt)("p",null,"If a variable name or a context key contains any special character (e.g. whitespace, dash, etc.)\nthen the name can be wrapped into single backquotes/backticks (e.g. ",(0,r.kt)("inlineCode",{parentName:"p"},"`foo bar`"),")."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"`first name`\n\n`tracking-id`\n\norder.`total price`\n")),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"Use the ",(0,r.kt)("a",{parentName:"p",href:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-context#get-value"},(0,r.kt)("inlineCode",{parentName:"a"},"get value()"))," function\nto retrieve the context value of an arbitrary key."),(0,r.kt)("pre",{parentName:"admonition"},(0,r.kt)("code",{parentName:"pre",className:"language-js"},'get value(order, "total price")\n'))))}d.isMDXComponent=!0}}]);