-- Table: public.account

-- DROP TABLE public.account;

CREATE TABLE public.account
(
  id bigint NOT NULL,
  account_number character varying(255) NOT NULL,
  balance bigint NOT NULL,
  is_active boolean,
  person_id bigint NOT NULL,
  CONSTRAINT account_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.account
  OWNER TO postgres;
  
  
--data insertion  
INSERT INTO public.account(
            id, account_number, balance, is_active, person_id)
    VALUES (1, 8776672, 1000, true, 1);

INSERT INTO public.account(
            id, account_number, balance, is_active, person_id)
    VALUES (2, 36435552, 0, true, 2);

INSERT INTO public.account(
            id, account_number, balance, is_active, person_id)
    VALUES (3, 754632562, 500, true, 3);

INSERT INTO public.account(
            id, account_number, balance, is_active, person_id)
    VALUES (4, 3473265, 220, true, 4);              
	
	
	
	
-- Table: public.card

-- DROP TABLE public.card;

CREATE TABLE public.card
(
  id bigint NOT NULL,
  card_number character varying(255) NOT NULL,
  cvv2 integer NOT NULL,
  expire_date timestamp without time zone NOT NULL,
  is_active boolean,
  pin character varying(255) NOT NULL,
  account_id bigint NOT NULL,
  incorrect_pin_count integer,
  CONSTRAINT card_pkey PRIMARY KEY (id),
  CONSTRAINT fk8v67eys6tqflsm6hrdgru2phu FOREIGN KEY (account_id)
      REFERENCES public.account (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.card
  OWNER TO postgres;
	
	
	
-- CARD DATA INSERTION	
INSERT INTO public.card(
            id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count)
    VALUES (1, 6280231451904303, 199, '2023/11/11', true, 1233, 1, 0);

INSERT INTO public.card(
            id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count)
    VALUES (2, 628023145234765, 342, '2023/11/11', false, 1111, 2, 0);

INSERT INTO public.card(
            id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count)
    VALUES (3, 628023145412347, 202, '2020/11/11', true, 7654, 1, 0);

INSERT INTO public.card(
            id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count)
    VALUES (4, 6280231476123412, 239, '2023/11/11', true, 1233, 1, 0);
