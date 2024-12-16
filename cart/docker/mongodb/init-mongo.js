db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "cartdb"
            },
            {
                role: "dbAdmin",
                db: "cartdb"
            }
        ]
    }
);
