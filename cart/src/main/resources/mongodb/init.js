db = db.getSiblingDB('cartdb');

// Criar coleção de carrinhos
db.createCollection('carts');

// Índice para busca por userId
db.carts.createIndex({ "userId": 1 });

// Índice composto para busca por status e data de atualização
db.carts.createIndex({ "status": 1, "updatedAt": -1 });

// Índice para busca por items.productId
db.carts.createIndex({ "items.productId": 1 });

// Índice TTL para limpar carrinhos abandonados após 30 dias
db.carts.createIndex(
    { "updatedAt": 1 },
    { expireAfterSeconds: 2592000, // 30 dias
      partialFilterExpression: { "status": "ABANDONED" }
    }
);
