db.createUser({ user: 'bs', pwd: 'bs', roles: [ { role: 'readWrite', db: 'bloodstream' } ] });
rs.initiate();

