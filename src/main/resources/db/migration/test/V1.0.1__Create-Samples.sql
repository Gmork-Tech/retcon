INSERT INTO Application (id, name, optimizable)
VALUES ('123e4567-e89b-12d3-a456-426655440000', 'Sample Application 1', true),
        ('22450c8a-1d2e-4a18-ae4d-593065e1a68e', 'Sample Application 2', true),
        ('b7b91be1-94d7-4d55-b0a9-4ad277065006', 'Sample Application 3', true),
        ('a2d67c87-a3de-4863-ba1a-9740a9164b64', 'Sample Application 4', true),
        ('0b68cb0a-b6b0-41ab-b99d-a2865b54dffe', 'Sample Application 5', true),
        ('3db4ebac-af3c-4b09-b899-657c25a94e63', 'Sample Application 6', true),
        ('b1401919-9b36-4fdd-9570-3c5d3ee033a5', 'Sample Application 7', true),
        ('ed7b815c-4dbf-4cc2-8b37-f66b63b93b47', 'Sample Application 8', true),
        ('ec1c8521-d7db-4606-bb95-39015c9df4dc', 'Sample Application 9', true),
        ('40211fd6-66c9-44e8-9182-59fd40782fc3', 'Sample Application 10', true),
        ('40362443-fe37-4516-a98b-c3bd75da283e', 'Sample Application 11', true),
        ('c747b186-1b63-47e7-ae3a-8e444103bc3a', 'Sample Application 12', true),
        ('00dc68f5-8aa2-4fd4-af8f-5a2000436edf', 'Sample Application 13', true),
        ('ae2719ea-deed-4d9c-9189-1ed36aad8ec8', 'Sample Application 14', true),
        ('ed08474e-603a-4f80-8cbf-6721c9e42a33', 'Sample Application 15', true),
        ('ec7434e8-f6a6-45b9-b9bc-64f0cc2f443f', 'Sample Application 16', true),
        ('d0629981-9fcf-4971-a01a-2c7f691da512', 'Sample Application 17', true),
        ('748a5cc6-4cfb-4b4d-97d3-46590ee16dad', 'Sample Application 18', true),
        ('48f2469f-d541-4801-83b8-fa3ec11524d9', 'Sample Application 19', true),
        ('d4c9367c-1405-44ad-bdc2-f81eef90547e', 'Sample Application 20', true),
        ('5c095d8e-f098-4d69-938f-dbd0156ef024', 'Sample Application 21', true),
        ('59862a20-9729-4826-af8b-c0d5da322502', 'Sample Application 22', true),
        ('3f3c1816-5bab-41e5-9fe1-5a10ddbbd88b', 'Sample Application 23', true),
        ('3a09c458-0618-43b1-b486-feb01b814f8e', 'Sample Application 24', true),
        ('5aeb668b-e498-48c8-b418-7c3510406838', 'Sample Application 25', true),
        ('4182f881-3729-445f-9d15-f601375d718e', 'Sample Application 26', true),
        ('c259e61b-f228-4131-93fe-c814514a2c92', 'Sample Application 27', true),
        ('85d03e6f-352f-46e6-9324-3b216735f865', 'Sample Application 28', true),
        ('22679d62-975d-415c-a551-c638c34d9485', 'Sample Application 29', true),
        ('0e555e20-f93c-42d7-989f-548635372654', 'Sample Application 30', true),
        ('2871084a-86a3-44bb-a0a2-988408b61d63', 'Sample Application 31', true),
        ('4f47052f-5040-496e-ab4d-e3156d982299', 'Sample Application 32', true),
        ('653d3880-241e-4619-827d-7b80b053530d', 'Sample Application 33', true),
        ('1d24b177-6f93-4853-b6df-423860470973', 'Sample Application 34', true),
        ('c723112d-727f-4338-803e-110959025874', 'Sample Application 35', true),
        ('0c17174b-334f-440b-b694-403650917304', 'Sample Application 36', true),
        ('22557993-b001-483c-a89b-3e67074a3096', 'Sample Application 37', true),
        ('f240b20f-070b-4c0b-b716-505035216381', 'Sample Application 38', true),
        ('c1408778-a7a2-4bce-b975-e63a6c071304', 'Sample Application 39', true),
        ('232e1738-001f-4d21-b786-a36556933e13', 'Sample Application 40', true),
        ('4e280d86-663c-4843-8e05-704b42621212', 'Sample Application 41', true),
        ('54504258-88d3-429c-8946-d5653113219a', 'Sample Application 42', true),
        ('194b1825-54a6-4ad4-a275-765208414359', 'Sample Application 43', true),
        ('3269361a-0894-4889-9487-f13212340964', 'Sample Application 44', true),
        ('77664c35-d335-434c-966f-453456789012', 'Sample Application 45', true),
        ('93457268-67d9-4321-9dfc-213578923045', 'Sample Application 46', true),
        ('51e4798f-0774-48c3-9a0b-e54657890123', 'Sample Application 47', true),
        ('f2a38215-697d-48ab-8234-567890123456', 'Sample Application 48', true),
        ('74819236-d5d6-4321-b876-578901234567', 'Sample Application 49', true),
        ('b9461032-a15e-4243-8790-589012345678', 'Sample Application 50', true);

-- Deployment 1: Canary
INSERT INTO Deployment (priority, convertToFull, incrementDelay, incrementPercentage, incrementQuantity, initialPercentage, initialQuantity, shouldIncrement, targetPercentage, targetQuantity, applicationId, kind, name)
VALUES (1, false, 300000000000, 20, 10, 50, 100, true, 80, 150, '123e4567-e89b-12d3-a456-426655440000', 'BY_QUANTITY', 'Deployment 1'),
       (1, false, 300000000000, 30, 20, 20, 200, true, 60, 300, '123e4567-e89b-12d3-a456-426655440000', 'MANUAL', 'Deployment 2'),
       (1, false, 300000000000, 40, 30, 10, 400, true, 40, 600, '123e4567-e89b-12d3-a456-426655440000', 'FULL', 'Deployment 3');

-- Deployment 1: Canary
INSERT INTO ConfigProp (created, nullable, deploymentId, name, val, kind)
VALUES ('2023-12-30T16:26:04Z', false, 1, 'prop1', 'value1', 'STRING'),
       ('2023-12-30T16:26:04Z', true, 1, 'prop2', 'value3', 'STRING'),
       ('2023-12-30T16:26:04Z', false, 1, 'prop3', 'value4', 'STRING');

-- Deployment 2: Staging
INSERT INTO ConfigProp (created, nullable, deploymentId, name, val, kind)
VALUES ('2023-12-30T16:26:04Z', false, 2, 'prop4', '5', 'NUMBER'),
       ('2023-12-30T16:26:04Z', true, 2, 'prop5', '6', 'NUMBER'),
       ('2023-12-30T16:26:04Z', false, 2, 'prop6', '7', 'NUMBER');

-- Deployment 3: Production
INSERT INTO ConfigProp (created, nullable, deploymentId, name, val, kind)
VALUES ('2023-12-30T16:26:04Z', false, 3, 'prop7', 'true', 'BOOLEAN'),
       ('2023-12-30T16:26:04Z', true, 3, 'prop8', '{"someName":"someValue"}', 'OBJECT'),
       ('2023-12-30T16:26:04Z', false, 3, 'prop9', '[{"someName":"someValue"},{"someName":"someValue"}]', 'ARRAY'),
       ('2023-12-30T16:26:04Z', false, 3, 'prop10', '[1,2,3,4]', 'ARRAY');
