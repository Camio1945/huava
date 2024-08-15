---

# Folder Explain

This folder's classes help Jackson deserialize objects.

---

## deserializer

When we store oauth2 info in the database, we need to serialize it into JSON format (say, the oauth2_authorization's attributes column).

When refreshing a token, we need to deserialize it from JSON format back to objects.

But the wield thing is, we need a lot more effort for deserializing than serializing.

This folder contains some deserializers; they cannot be used alone; they are all used in the next `mixin` folder.

---

## mixin

The main purpose of this folder is to avoid this kind of exception.

```
java.lang.IllegalArgumentException: The class with org.springframework.security.oauth2.server.authorization.authentication. OAuth2ClientAuthenticationToken and name of org.springframework.security.oauth2.server.authorization.authentication. OAuth2ClientAuthenticationToken is not in the allowlist. If you believe this class is safe to deserialize, please provide an explicit mapping using Jackson annotations or by providing a mixin.
```

Jackson does not trust all classes by default; sometimes we need to explicitly tell it to trust our custom classes and tell it which deserializer to use.

This folder cannot be used alone; all classes in it are used in the next `module` folder.

---

## module

We need to create a module to register the deserializers and mixins.

This folder has only one class, and this class is the facade of the whole `jackson` folder.

